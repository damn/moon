(ns clojure.moon.create-audio
  (:require [com.badlogic.gdx.audio :as audio]
            [clojure.edn :as edn]
            [com.badlogic.gdx.files :as files]
            [clojure.java.io :as io]))

(defn f [ctx]
  (assoc ctx
         :ctx/audio (into {}
                          (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
                                :let [path (format "sounds/%s.wav" sound-name)]]
                            [sound-name
                             (audio/newSound (:ctx/audio ctx)
                                              (files/internal (:ctx/files ctx) path))]))))
