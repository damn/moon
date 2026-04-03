(ns moon.create.audio
  (:require [clj.api.com.badlogic.gdx.audio :as audio]
            [clj.api.com.badlogic.gdx.files :as files]))

(defn do!
  [ctx {:keys [sound-names path-format]}]
  (assoc ctx :ctx/audio (let [sound-name->file-handle (into {}
                                                            (for [sound-name sound-names
                                                                  :let [path (format path-format sound-name)]]
                                                              [sound-name
                                                               (files/internal (:ctx/files ctx) path)]))]
                          (into {}
                                (for [[sound-name file-handle] sound-name->file-handle]
                                  [sound-name
                                   (audio/new-sound (:ctx/audio ctx) file-handle)])))))
