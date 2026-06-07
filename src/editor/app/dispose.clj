(ns editor.app.dispose
  (:require [com.badlogic.gdx.utils.dispose :as disposable]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch]}]
  ; TODO textures not disposede
  (disposable/dispose! skin)
  (disposable/dispose! batch))
