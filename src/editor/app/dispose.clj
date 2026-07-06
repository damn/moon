(ns editor.app.dispose
  (:require [com.badlogic.gdx.utils.disposable :as disposable]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch]}]
  ; TODO textures not disposede
  (disposable/dispose! skin)
  (disposable/dispose! batch))
