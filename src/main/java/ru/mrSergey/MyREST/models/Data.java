package ru.mrSergey.MyREST.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data")
public class Data {
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(name = "value")
        private int value;

        @Column(name = "raining")
        private boolean raining;

        @ManyToOne
        @JoinColumn(name = "id_sensor", referencedColumnName = "id")
        private Sensor owner;

        @Column(name = "time")
        private LocalDateTime time;

        public Data(int value, boolean raining, LocalDateTime time) {
                this.value = value;
                this.raining = raining;
                this.time = time;
        }
        public Data(){}

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public int getValue() {
                return value;
        }

        public void setValue(int value) {
                this.value = value;
        }

        public boolean getRaining() {
                return raining;
        }

        public void setRaining(boolean raining) {
                this.raining = raining;
        }

        public Sensor getOwner() {
                return owner;
        }

        public void setOwner(Sensor owner) {
                this.owner = owner;
        }

        public LocalDateTime getTime() {
                return time;
        }

        public void setTime(LocalDateTime time) {
                this.time = time;
        }
}
