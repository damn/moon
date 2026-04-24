(ns moon.create.sounds
  (:require [clojure.audio :as audio]
            [clojure.files :as files]))

(defn step
  [{:keys [ctx/audio
           ctx/files] :as ctx}
   {:keys [sound-names path-format]}]
  (assoc ctx :ctx/audio
         (let [sound-name->file-handle (into {}
                                             (for [sound-name sound-names
                                                   :let [path (format path-format sound-name)]]
                                               [sound-name
                                                (files/internal files path)]))]
           (into {}
                 (for [[sound-name file-handle] sound-name->file-handle]
                   [sound-name
                    (audio/new-sound audio file-handle)])))))
