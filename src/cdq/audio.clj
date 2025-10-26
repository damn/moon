(ns cdq.audio
  (:require [moon.audio :as audio]
            [moon.audio.sound :as sound]
            [moon.files :as files]))

(defn sound-names [sounds]
  (map first sounds))

(defn play! [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (sound/play! (get sounds sound-name)))

(defn dispose! [sounds]
  (run! sound/dispose! (vals sounds)))

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
             (audio/sound audio file-handle)]))))
