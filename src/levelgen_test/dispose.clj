(ns levelgen-test.dispose
  (:import (com.badlogic.gdx.utils Disposable)))

(defn f!
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  ; TODO TEXTURES NOT DISPOSED
  (Disposable/.dispose skin)
  (Disposable/.dispose sprite-batch)
  (Disposable/.dispose tiled-map))
