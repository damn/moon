(ns moon.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.badlogic.gdx.audio :as gdx-audio]
            [com.badlogic.gdx.audio.sound :as sound]
            [gdx.files :as files]
            [gdx.utils.disposable :as disposable]))

(defn create
  [audio files]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (gdx-audio/newSound audio (files/internal files path))])))

(defn play!
  [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (sound/play (get sounds sound-name)))

(defn names
  [sounds]
  (keys sounds))

(defn dispose!
  [sounds]
  (run! disposable/dispose! (vals sounds)))
