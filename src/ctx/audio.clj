(ns ctx.audio
  (:require [clojure.edn :as edn]
            [clojure.gdx :as gdx]
            [clojure.java.io :as io]))

(defn step
  [{:keys [ctx/audio
           ctx/files]}]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (gdx/new-sound audio
                          (gdx/internal files path))])))
