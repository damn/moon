(ns create.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.badlogic.gdx.audio :as audio]
            [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files]))

(defn step [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/audio (into {}
                              (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
                                    :let [path (format "sounds/%s.wav" sound-name)]]
                                [sound-name
                                 (audio/new-sound (app/audio app)
                                                  (files/internal (app/files app) path))]))))
