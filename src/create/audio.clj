(ns create.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [gdx.app :as app]))

(defn step [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/audio (into {}
                              (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)]
                                [sound-name
                                 (app/new-sound app (format "sounds/%s.wav" sound-name))]))))
