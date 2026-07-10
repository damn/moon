(ns clojure.editor.dispose
  (:require [com.badlogic.gdx.utils.disposable :as disposable]))

(defn dispose [{:keys [ctx/skin
                             ctx/batch
                             ctx/textures]}]
  (disposable/dispose batch)
  (disposable/dispose skin)
  (run! disposable/dispose (vals textures)))
