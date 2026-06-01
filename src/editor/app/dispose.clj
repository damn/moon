(ns editor.app.dispose
  (:require [clojure.gdx.utils.disposable :as disposable]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch]}]
  ; TODO textures not disposede
  (disposable/dispose! skin)
  (disposable/dispose! batch))
