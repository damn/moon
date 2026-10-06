(ns game.listener.dispose
  (:require [game.shared :refer [audio
                                 batch
                                 cursors
                                 default-font
                                 shape-drawer-texture
                                 skin
                                 textures
                                 tiled-map]])
  (:import (com.badlogic.gdx.utils Disposable)))

(defn dispose! []
  (run! Disposable/.dispose (vals @audio))
  (Disposable/.dispose @batch)
  (run! Disposable/.dispose (vals @cursors))
  (Disposable/.dispose @default-font)
  (Disposable/.dispose @shape-drawer-texture)
  (Disposable/.dispose @skin)
  (run! Disposable/.dispose (vals @textures))
  (Disposable/.dispose @tiled-map))
