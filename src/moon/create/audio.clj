(ns moon.create.audio
  (:require [clj.api.com.badlogic.gdx.audio :as audio]
            [clj.api.com.badlogic.gdx.files :as files]))

(defn- load-sounds*
  [audio files {:keys [sound-names path-format]}]
  (let [sound-name->file-handle (into {}
                                      (for [sound-name sound-names
                                            :let [path (format path-format sound-name)]]
                                        [sound-name
                                         (files/internal files path)]))]
    (into {}
          (for [[sound-name file-handle] sound-name->file-handle]
            [sound-name
             (audio/new-sound audio file-handle)]))))

(defn do!
  [{:keys [ctx/audio
           ctx/files]
    :as ctx}
   config]
  (assoc ctx :ctx/audio (load-sounds* audio files config)))
