(ns moon.graphics.impl
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport]
            [clj.api.space.earlygrey.shape-drawer :as sd]
            [moon.graphics :as graphics]
            [moon.graphics.camera :as camera]
            [moon.tm-renderer :as tm-renderer])
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics Color
                                      Colors
                                      OrthographicCamera
                                      Pixmap
                                      Pixmap$Format
                                      Texture
                                      Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d Batch
                                          SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.utils Disposable
                                   ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport
                                            Viewport)))

(def draw-fns
  (update-vals '{:draw/arc              moon.draw.arc/do!
                 :draw/circle           moon.draw.circle/do!
                 :draw/ellipse          moon.draw.ellipse/do!
                 :draw/filled-circle    moon.draw.filled-circle/do!
                 :draw/filled-ellipse   moon.draw.filled-ellipse/do!
                 :draw/filled-rectangle moon.draw.filled-rectangle/do!
                 :draw/grid             moon.draw.grid/do!
                 :draw/line             moon.draw.line/do!
                 :draw/rectangle        moon.draw.rectangle/do!
                 :draw/sector           moon.draw.sector/do!
                 :draw/text             moon.draw.text/do!
                 :draw/texture-region   moon.draw.texture-region/do!
                 :draw/with-line-width  moon.draw.with-line-width/do!}
               requiring-resolve))

(defrecord RGraphics []
  moon.graphics/Graphics
  (unproject-ui [{:keys [graphics/ui-viewport]} position]
    (viewport/unproject ui-viewport position))

  (update-ui-viewport! [{:keys [graphics/ui-viewport]} width height]
    (FitViewport/.update ui-viewport width height true))

  (dispose!
    [{:keys [graphics/batch
             graphics/cursors
             graphics/default-font
             graphics/shape-drawer-texture]}]
    (Disposable/.dispose batch)
    (run! Disposable/.dispose (vals cursors))
    (Disposable/.dispose default-font)
    (Disposable/.dispose shape-drawer-texture))

  (frames-per-second [{:keys [graphics/core]}]
    (Graphics/.getFramesPerSecond core))

  (delta-time [{:keys [graphics/core]}]
    (Graphics/.getDeltaTime core))

  (clear-screen! [_ [r g b a]]
    (ScreenUtils/clear r g b a))

  (set-cursor! [{:keys [graphics/core
                        graphics/cursors]}
                cursor-key]
    (assert (contains? cursors cursor-key))
    (Graphics/.setCursor core (get cursors cursor-key)))

  (draw! [graphics draws]
    (doseq [{k 0 :as component} draws
            :when component]
      (apply (get draw-fns k) graphics (rest component))))

  (draw-tiled-map!
    [{:keys [graphics/tiled-map-renderer
             graphics/world-viewport]}
     tiled-map
     color-setter]
    (tm-renderer/draw! tiled-map-renderer
                       world-viewport
                       tiled-map
                       color-setter))

  (position [{:keys [graphics/world-viewport]}]
    (camera/position (Viewport/.getCamera world-viewport)))

  (visible-tiles [{:keys [graphics/world-viewport]}]
    (camera/visible-tiles (Viewport/.getCamera world-viewport)))

  (frustum [{:keys [graphics/world-viewport]}]
    (camera/frustum (Viewport/.getCamera world-viewport)))

  (zoom [{:keys [graphics/world-viewport]}]
    (camera/zoom (Viewport/.getCamera world-viewport)))

  (change-zoom! [{:keys [graphics/world-viewport]} amount]
    (camera/inc-zoom! (Viewport/.getCamera world-viewport) amount))

  (set-position! [{:keys [graphics/world-viewport]} position]
    (camera/set-position! (Viewport/.getCamera world-viewport) position))

  (world-vp-width [{:keys [graphics/world-viewport]}]
    (Viewport/.getWorldWidth world-viewport))

  (world-vp-height [{:keys [graphics/world-viewport]}]
    (Viewport/.getWorldHeight world-viewport))

  (unproject-world [{:keys [graphics/world-viewport]} position]
    (viewport/unproject world-viewport position))

  (update-world-vp! [{:keys [graphics/world-viewport]} width height]
    (Viewport/.update world-viewport width height false))

  (draw-on-world-vp!
    [{:keys [^Batch graphics/batch
             graphics/shape-drawer
             graphics/unit-scale
             graphics/world-unit-scale
             graphics/world-viewport]}
     f]
    ; fix scene2d.ui.tooltip flickering
    ; _everything_ flickers with vis ui tooltip! it changes batch color somehow and does not
    ; change it back !
    (.setColor batch 1 1 1 1)
    ;
    (.setProjectionMatrix batch (camera/combined (Viewport/.getCamera world-viewport)))
    (.begin batch)
    (sd/with-line-width shape-drawer world-unit-scale
      (reset! unit-scale world-unit-scale)
      (f)
      (reset! unit-scale 1))
    (.end batch)))

(defn- create-cursor [files graphics path [hotspot-x hotspot-y]]
  (let [pixmap (Pixmap. (.internal files path))
        cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
    (.dispose pixmap)
    cursor))

(defn- generate-font
  [file-handle {:keys [size
                       quality-scaling
                       enable-markup?
                       use-integer-positions?]}]
  (let [generator (FreeTypeFontGenerator. file-handle)
        font (.generateFont generator
                            (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                              (set! (.size params) (* size quality-scaling))
                              (set! (.minFilter params) Texture$TextureFilter/Linear)
                              (set! (.magFilter params) Texture$TextureFilter/Linear)
                              params))]
    (.dispose generator)
    (.setScale (.getData font) (/ quality-scaling))
    (set! (.markupEnabled (.getData font)) enable-markup?)
    (.setUseIntegerPositions font use-integer-positions?)
    font))

(defn create!
  [graphics
   files
   {:keys [colors
           cursors
           default-font
           texture-folder
           tile-size
           ui-viewport
           world-viewport]}]
  (doseq [[name [r g b a]] colors]
    (Colors/put name (Color. r g b a)))
  (let [batch (SpriteBatch.)
        shape-drawer-texture (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                                            (.setColor 1 1 1 1)
                                            (.drawPixel 0 0))
                                   texture (Texture. pixmap)]
                               (.dispose pixmap)
                               texture)
        world-unit-scale (float (/ tile-size))]
    (-> (map->RGraphics {})
        (assoc :graphics/core graphics)
        (assoc :graphics/cursors (update-vals (:data cursors)
                                              (fn [[path hotspot]]
                                                (create-cursor files
                                                               graphics
                                                               (format (:path-format cursors) path)
                                                               hotspot))))
        (assoc :graphics/default-font (generate-font (.internal files (:path default-font))
                                                     (:params default-font)))
        (assoc :graphics/batch batch)
        (assoc :graphics/shape-drawer-texture shape-drawer-texture)
        (assoc :graphics/shape-drawer (sd/create batch (TextureRegion. shape-drawer-texture 1 0 1 1)))
        (assoc :graphics/unit-scale (atom 1)
               :graphics/world-unit-scale world-unit-scale)
        (assoc :graphics/tiled-map-renderer (tm-renderer/create world-unit-scale batch))
        (assoc :graphics/ui-viewport (FitViewport. (:width  ui-viewport)
                                                   (:height ui-viewport)
                                                   (OrthographicCamera.)))
        (assoc :graphics/world-viewport (let [world-width  (* (:width  world-viewport) world-unit-scale)
                                              world-height (* (:height world-viewport) world-unit-scale)]
                                          (FitViewport. world-width
                                                        world-height
                                                        (doto (OrthographicCamera.)
                                                          (.setToOrtho false world-width world-height))))))))
