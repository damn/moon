(ns moon.application.create.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.audio :as audio]))

(defn step
  [ctx]
  (assoc ctx :ctx/audio (into {}
                              (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                                [sound-name
                                 (audio/new-sound ctx (format "sounds/%s.wav" sound-name))]))))
