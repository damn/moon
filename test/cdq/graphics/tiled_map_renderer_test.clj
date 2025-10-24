#_(ns cdq.graphics.tiled-map-renderer-test
  (:require [clojure.gdx.backends.lwjgl.application :as application]
            [com.badlogic.gdx.maps.tiled :as tiled]
            [com.badlogic.gdx.graphics.orthographic-camera :as camera]
            [clojure.gdx.maps.tiled.tmx :as tmx])
  (:import (com.badlogic.gdx.graphics Color OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)))

(def screen-width 800)
(def screen-height 600)

(def world-unit-scale (float (/ 48)))

(def tiled-map-path "maps/vampire.tmx")

(def camera-position [32 71])

#_(defn- color-setter [_color _x _y]
  Color/WHITE)

#_(defn -main []
  (clojure.lwjgl.system.configuration/set-glfw-library-name! "glfw_async")
  (application/create
   (proxy [ApplicationAdapter] []
     (create []
       (def tiled-map (tmx/load-map tiled-map-path))
       (def batch (SpriteBatch.))
       (def camera (doto (OrthographicCamera.)
                     (.setToOrtho false ; y-down?
                                  (* screen-width world-unit-scale)
                                  (* screen-height world-unit-scale))))
       (camera/set-position! camera camera-position)
       (def renderer (tiled/renderer tiled-map world-unit-scale batch)))

     (dispose []
       (Disposable/.dispose tiled-map)
       (Disposable/.dispose batch))

     (render []
       (tiled/draw! renderer tiled-map color-setter camera))

     (resize [width height]))))
