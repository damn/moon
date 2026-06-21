(ns create.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.audio :as audio]
            [clojure.files :as files]))

(defn step
  [{:keys [ctx/audio
           ctx/files]}]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (audio/new-sound audio
                            (files/internal files path))])))
