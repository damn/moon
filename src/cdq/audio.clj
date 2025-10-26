(ns cdq.audio
  (:require [moon.files :as files])
  (:import (com.badlogic.gdx Audio)
           (com.badlogic.gdx.audio Sound)))

(defn sound-names [sounds]
  (map first sounds))

(defn play! [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (Sound/.play (get sounds sound-name)))

(defn dispose! [sounds]
  (run! Sound/.dispose (vals sounds)))

(defn create
  [audio files {:keys [sound-names path-format]}]
  (let [sound-name->file-handle (into {}
                                      (for [sound-name sound-names
                                            :let [path (format path-format sound-name)]]
                                        [sound-name
                                         (files/internal files path)]))]
    (into {}
          (for [[sound-name file-handle] sound-name->file-handle]
            [sound-name
             (Audio/.newSound audio file-handle)]))))
