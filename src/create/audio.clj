(ns create.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [gdx.application :as app]
            [gdx.files :as files]))

(defn step [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/audio (into {}
                              (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                                [sound-name
                                 (.newSound (app/audio app)
                                            (files/internal (app/files app) (format "sounds/%s.wav" sound-name)))]))))
