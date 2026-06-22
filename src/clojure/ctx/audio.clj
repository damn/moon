(ns clojure.ctx.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [gdl.audio.new-sound :as new-sound]
            [gdl.files.internal :as internal]))

(defn step
  [{:keys [ctx/audio
           ctx/files]}]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (new-sound/f audio
                        (internal/f files path))])))
