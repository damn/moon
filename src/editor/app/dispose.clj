(ns editor.app.dispose
  (:require [clojure.gdx.disposable.dispose :as dispose]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch]}]
  ; TODO textures not disposede
  (dispose/f skin)
  (dispose/f batch))
