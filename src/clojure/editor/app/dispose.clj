(ns clojure.editor.app.dispose
  (:require [gdl.dispose :as disposable]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch]}]
  ; TODO textures not disposede
  (disposable/dispose! skin)
  (disposable/dispose! batch))
