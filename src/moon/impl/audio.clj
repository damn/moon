(ns moon.impl.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.audio :as audio]))

(defn create
  [{:keys [ctx/app]}]
  (into {}
        (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
          [sound-name
           (audio/new-sound app (format "sounds/%s.wav" sound-name))])))
