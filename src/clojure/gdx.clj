(ns clojure.gdx
  (:require [clojure.graphics.batch :as batch]
            [clojure.gdx.graphics.color :as color]
            [clojure.graphics.pixmap :as pixmap]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.group :as group]
            [clojure.scene2d.stage :as stage]
            [clojure.utils.viewport :as viewport]
            clojure.gdx.graphics
            clojure.gdx.input
            clojure.gdx.scenes.scene2d.actor
            clojure.gdx.scenes.scene2d.group
            clojure.gdx.scenes.scene2d.event
            clojure.gdx.scenes.scene2d.ui.widget
            clojure.gdx.scenes.scene2d.ui.scroll-pane
            clojure.gdx.scenes.scene2d.ui.horizontal-group
            clojure.gdx.scenes.scene2d.ui.label
            clojure.gdx.scenes.scene2d.ui.image-button
            clojure.gdx.scenes.scene2d.ui.select-box
            clojure.gdx.scenes.scene2d.ui.stack
            clojure.gdx.scenes.scene2d.ui.image
            clojure.gdx.scenes.scene2d.ui.text-button
            clojure.gdx.scenes.scene2d.ui.text-field
            clojure.gdx.scenes.scene2d.ui.text-tooltip
            clojure.gdx.scenes.scene2d.ui.table
            clojure.gdx.scenes.scene2d.ui.image
            clojure.gdx.scenes.scene2d.ui.check-box
            clojure.gdx.scenes.scene2d.ui.window
            clojure.gdx.scenes.scene2d.ui.widget-group
            clojure.gdx.scenes.scene2d.utils.texture-region-drawable
            clojure.gdx.scenes.scene2d.utils.change-listener
            clojure.gdx.scenes.scene2d.utils.click-listener
            clojure.gdx.maps.map-layers
            clojure.gdx.maps.map-properties
            clojure.gdx.maps.tiled.tiled-map
            clojure.gdx.maps.tiled.tiled-map-tile-layer
            clojure.gdx.maps.renderer
            clojure.gdx.utils.disposable
            [clojure.gdx.math.vector2 :as vector2]
            [clojure.gdx.math.vector3 :as vector3])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Colors
                                      Pixmap
                                      Pixmap$Format
                                      Texture
                                      OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.scenes.scene2d CtxStage)
           (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)
           (com.badlogic.gdx.utils ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn application!
  [{:keys [title
           windowed-mode
           foreground-fps
           create!
           dispose!
           render!
           resize!
           pause!
           resume!]}]
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (create! Gdx/app))

                        (dispose [_]
                          (dispose!))

                        (render [_]
                          (render!))

                        (resize [_ width height]
                          (resize! width height))

                        (pause [_]
                          (pause!))

                        (resume [_]
                          (resume!)))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                        (.setForegroundFPS foreground-fps))))

(defn put-colors! [colors]
  (doseq [[name rgba] colors]
    (Colors/put name (color/create rgba))))

(defn pixmap [w h]
  (Pixmap. (int w) (int h) Pixmap$Format/RGBA8888))

(defn orthographic-camera
  [{:keys [y-down?
           world-width
           world-height]}]
  (doto (OrthographicCamera.)
    (.setToOrtho y-down? world-width world-height)))

(extend-type OrthographicCamera
  camera/OrthographicCamera
  (viewport-width [camera]
    (.viewportWidth camera))

  (viewport-height [camera]
    (.viewportHeight camera))

  (combined [camera]
    (.combined camera))

  (zoom [camera]
    (.zoom camera))

  (frustum [camera]
    (let [plane-points (mapv vector3/->clj (.planePoints (.frustum camera)))
          frustum-points (take 4 plane-points)
          left-x   (apply min (map first  frustum-points))
          right-x  (apply max (map first  frustum-points))
          bottom-y (apply min (map second frustum-points))
          top-y    (apply max (map second frustum-points))]
      [left-x right-x bottom-y top-y]))

  (position [camera]
    (vector3/->clj (.position camera)))

  (set-position! [camera [x y]]
    (set! (.x (.position camera)) x)
    (set! (.y (.position camera)) y)
    (.update camera))

  (set-zoom! [camera amount]
    (set! (.zoom camera) amount)
    (.update camera)))

(defn sprite-batch []
  (SpriteBatch.))

(extend-type SpriteBatch
  batch/Batch
  (begin! [batch]
    (.begin batch))

  (end! [batch]
    (.end batch))

  (set-color! [batch r g b a]
    (.setColor batch r g b a))

  (set-projection-matrix! [batch matrix]
    (.setProjectionMatrix batch matrix))

  (draw!
    ([batch texture-region x y origin-x origin-y width height scale-x scale-y rotation]
     (.draw batch
            ^TextureRegion texture-region
            x
            y
            origin-x
            origin-y
            width
            height
            scale-x
            scale-y
            rotation))
    ([batch texture-region x y w h]
     (.draw batch ^TextureRegion texture-region (float x) (float y) (float w) (float h)))))

(defn stage [viewport batch]
  (proxy [CtxStage ILookup] [viewport batch]
    (valAt [k]
      (case k
        ; TODO :stage/root
        :stage/ctx      (.ctx         ^CtxStage this)
        :stage/viewport (.getViewport ^CtxStage this)))))

(extend-type CtxStage
  stage/Stage
  (set-ctx! [stage ctx]
    (set! (.ctx stage) ctx))

  (add-actor! [stage actor]
    (.addActor stage (actor/create actor)))

  (act! [stage]
    (.act stage))

  (draw! [stage]
    (.draw stage))

  (find-actor [stage name]
    (-> stage
        .getRoot
        (group/find-actor name)))

  (mouseover-actor [stage position]
    (let [[x y] (-> stage :stage/viewport (viewport/unproject position))]
      (.hit stage x y true))))

(extend-type Pixmap
  pixmap/Pixmap
  (dispose! [pixmap]
    (.dispose pixmap))

  (set-color! [pixmap r g b a]
    (.setColor pixmap r g b a))

  (draw-pixel! [pixmap x y]
    (.drawPixel pixmap x y))

  (texture [pixmap]
    (Texture. pixmap)))

(defn clear-screen! [[r g b a]]
  (ScreenUtils/clear r g b a))

(defn fit-viewport
  ([width height]
   (proxy [FitViewport ILookup] [width height]
     (valAt [k]
       (case k
         :viewport/camera       (FitViewport/.getCamera      this)
         :viewport/world-width  (FitViewport/.getWorldWidth  this)
         :viewport/world-height (FitViewport/.getWorldHeight this)))))
  ([width height camera]
   (proxy [FitViewport ILookup] [width height camera]
     (valAt [k]
       (case k
         :viewport/camera       (FitViewport/.getCamera      this)
         :viewport/world-width  (FitViewport/.getWorldWidth  this)
         :viewport/world-height (FitViewport/.getWorldHeight this))))))

(extend-type FitViewport
  viewport/Viewport
  (update! [viewport screen-width screen-height center-camera?]
    (.update viewport screen-width screen-height center-camera?))

  (unproject [viewport position]
    (-> viewport
        (.unproject (vector2/->java position))
        vector2/->clj)))

(defn set-tooltip-initial-time! [value]
  (set! (.initialTime (TooltipManager/getInstance)) value))
