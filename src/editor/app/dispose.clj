(ns editor.app.dispose
  (:require [clojure.gdx :as gdx]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch]}]
  ; TODO textures not disposede
  (gdx/dispose! skin)
  (gdx/dispose! batch))
