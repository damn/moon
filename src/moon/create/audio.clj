(ns moon.create.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Audio
                             Files)))

(defn- load-sounds*
  [audio files {:keys [sound-names path-format]}]
  (let [sound-name->file-handle (into {}
                                      (for [sound-name (->> sound-names io/resource slurp edn/read-string)
                                            :let [path (format path-format sound-name)]]
                                        [sound-name
                                         (Files/.internal files path)]))]
    (into {}
          (for [[sound-name file-handle] sound-name->file-handle]
            [sound-name
             (Audio/.newSound audio file-handle)
             ;(audio/new-sound ctx file-handle)
             ]))))

(defn do!
  [{:keys [ctx/audio
           ctx/files]
    :as ctx}
   config]
  (assoc ctx :ctx/audio (load-sounds* audio files config)))

; TODO files stuff, one fn
