(ns clojure.editor.audio
  (:require [com.badlogic.gdx.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/audio (application/getAudio app)))
