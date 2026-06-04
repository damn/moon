(ns create.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [game.ctx.create-sound :refer [create-sound]]))

(defn step [ctx]
  (assoc ctx :ctx/audio (into {}
                              (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
                                    :let [path (format "sounds/%s.wav" sound-name)]]
                                [sound-name
                                 (create-sound ctx path)]))))
