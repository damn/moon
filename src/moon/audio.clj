(ns moon.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Audio Files)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.utils Disposable)))

(defn create
  [audio files]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (.newSound ^Audio audio (.internal ^Files files path))])))

(defn play!
  [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (.play ^Sound (get sounds sound-name)))

(defn names
  [sounds]
  (keys sounds))

(defn dispose!
  [sounds]
  (run! Disposable/.dispose (vals sounds)))
