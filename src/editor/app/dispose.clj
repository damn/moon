(ns editor.app.dispose
  (:import (com.badlogic.gdx.utils Disposable)))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch]}]
  ; TODO textures not disposede
  (Disposable/.dispose skin)
  (Disposable/.dispose batch))
