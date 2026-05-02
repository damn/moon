(ns moon.application.create.audio
  (:require [com.badlogic.gdx.application :as app]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/audio (into {}
                              (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                                [sound-name
                                 (.newSound (app/audio app) (.internal (app/files app) (format "sounds/%s.wav" sound-name)))]))))
