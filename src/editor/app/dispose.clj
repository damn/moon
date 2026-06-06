(ns editor.app.dispose
  (:require [gdx.dispose :as disposable]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch]}]
  ; TODO textures not disposede
  (disposable/dispose! skin)
  (disposable/dispose! batch))
