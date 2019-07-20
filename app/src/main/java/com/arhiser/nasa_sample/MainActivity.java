package com.arhiser.nasa_sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arhiser.nasa_sample.api.model.DateDTO;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    CompositeDisposable disposable = new CompositeDisposable();

    RecyclerView recyclerView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list);

        adapter = new Adapter();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setTitle(getString(R.string.choose_day));

        App app = (App) getApplication();

        disposable.add(app.getNasaService().getApi().getDatesWithPhoto()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<DateDTO>, Throwable>() {
                    @Override
                    public void accept(List<DateDTO> dates, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(MainActivity.this, "Data loading error", Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.setDates(dates);
                        }
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<DateDTO> dates = new ArrayList<>();

        public void setDates(List<DateDTO> dates) {
            this.dates.clear();
            this.dates.addAll(dates);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_date, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bind(dates.get(i));
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        DateDTO dateDTO;

        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoListActivity.start(view.getContext(), dateDTO.getDate());
                }
            });
        }

        public void bind(DateDTO date) {
            dateDTO = date;
            text.setText(date.getDate());
        }
    }
}
