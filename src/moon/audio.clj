(ns moon.audio
  (:require [clojure.edn :as edn]
            [gdx.sound :as sound]
            [gdx.files :as files]
            [gdx.disposable :as disposable]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Audio)))

(defn create
  [audio files]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (.newSound ^Audio audio (files/internal files path))])))

(defn play!
  [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (sound/play! (get sounds sound-name)))

(defn names
  [sounds]
  (keys sounds))

(defn dispose!
  [sounds]
  (run! disposable/dispose! (vals sounds)))
