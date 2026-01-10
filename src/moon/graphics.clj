(ns moon.graphics
  (:require [gdl.graphics :as graphics]
            [gdl.graphics.orthographic-camera :as orthographic-camera]
            [gdl.graphics.shape-drawer :as sd]
            [gdl.graphics.tm-renderer :as tm-renderer]
            [gdl.math.vector2 :as vector2]
            [gdl.utils.disposable :as disposable]
            [gdl.utils.screen :as screen-utils]
            [gdl.utils.viewport :as viewport]
            [gdl.utils.viewport.fit-viewport :as fit-viewport]
            [moon.files :as files-utils]
            [moon.graphics.camera :as camera])
  (:import (com.badlogic.gdx Gdx)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color
                                      Colors
                                      Pixmap
                                      Pixmap$Format
                                      Texture
                                      Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d Batch
                                          SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defprotocol Graphics
  (unproject-ui [_ position])
  (update-ui-viewport! [_ width height])
  (clear-screen! [_ color])
  (set-cursor! [_ cursor-key])
  (draw! [_ draws])
  (texture-region [_ image])
  (draw-tiled-map! [_ tiled-map color-setter])
  (frames-per-second [_])
  (delta-time [_])
  (dispose! [_])
  (position [_])
  (visible-tiles [_])
  (frustum [_])
  (zoom [_])
  (change-zoom! [_ amount])
  (set-position! [_ position])
  (world-vp-width [_])
  (world-vp-height [_])
  (unproject-world [_ position])
  (update-world-vp! [_ width height])
  (draw-on-world-vp! [_ f]))

(defn- unproject [viewport [x y]]
  (-> viewport
      (viewport/unproject (vector2/->java x y))
      vector2/->clj))

(defrecord RGraphics []
  Graphics
  (unproject-ui [{:keys [graphics/ui-viewport]} position]
    (unproject ui-viewport position))

  (update-ui-viewport! [{:keys [graphics/ui-viewport]} width height]
    (viewport/update! ui-viewport width height {:center? true}))

  (dispose!
    [{:keys [graphics/batch
             graphics/cursors
             graphics/default-font
             graphics/shape-drawer-texture
             graphics/textures]}]
    (disposable/dispose! batch)
    (run! disposable/dispose! (vals cursors))
    (disposable/dispose! default-font)
    (disposable/dispose! shape-drawer-texture)
    (run! disposable/dispose! (vals textures)))

  (frames-per-second [{:keys [graphics/core]}]
    (graphics/frames-per-second core))

  (delta-time [{:keys [graphics/core]}]
    (graphics/delta-time core))

  (clear-screen! [_ color]
    (screen-utils/clear! color))

  (set-cursor! [{:keys [graphics/core
                        graphics/cursors]}
                cursor-key]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! core (get cursors cursor-key)))

  (draw! [{:keys [graphics/draw-fns]
           :as graphics}
          draws]
    (doseq [{k 0 :as component} draws
            :when component]
      (apply (draw-fns k) graphics (rest component))))

  (texture-region [{:keys [graphics/textures]}
                   {:keys [image/file image/bounds]}]
    (assert file)
    (assert (contains? textures file))
    (let [^Texture texture (get textures file)]
      (if-let [[x y w h] bounds]
        (TextureRegion. texture (int x) (int y) (int w) (int h))
        (TextureRegion. texture))))

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
    (camera/position (viewport/camera world-viewport)))

  (visible-tiles [{:keys [graphics/world-viewport]}]
    (camera/visible-tiles (viewport/camera world-viewport)))

  (frustum [{:keys [graphics/world-viewport]}]
    (camera/frustum (viewport/camera world-viewport)))

  (zoom [{:keys [graphics/world-viewport]}]
    (camera/zoom (viewport/camera world-viewport)))

  (change-zoom! [{:keys [graphics/world-viewport]} amount]
    (camera/inc-zoom! (viewport/camera world-viewport) amount))

  (set-position! [{:keys [graphics/world-viewport]} position]
    (camera/set-position! (viewport/camera world-viewport) position))

  (world-vp-width [{:keys [graphics/world-viewport]}]
    (viewport/world-width world-viewport))

  (world-vp-height [{:keys [graphics/world-viewport]}]
    (viewport/world-height world-viewport))

  (unproject-world [{:keys [graphics/world-viewport]} position]
    (unproject world-viewport position))

  (update-world-vp! [{:keys [graphics/world-viewport]} width height]
    (viewport/update! world-viewport width height {:center? false}))

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
    (.setProjectionMatrix batch (camera/combined (viewport/camera world-viewport)))
    (.begin batch)
    (sd/with-line-width shape-drawer world-unit-scale
      (reset! unit-scale world-unit-scale)
      (f)
      (reset! unit-scale 1))
    (.end batch)))

(defn- create-cursor [files graphics path [hotspot-x hotspot-y]]
  (let [pixmap (Pixmap. (.internal Gdx/files path))
        cursor (graphics/new-cursor graphics pixmap hotspot-x hotspot-y)]
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
           world-viewport
           draw-fns]}]
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
        (assoc :graphics/default-font (generate-font (.internal Gdx/files (:path default-font))
                                                     (:params default-font)))
        (assoc :graphics/batch batch)
        (assoc :graphics/shape-drawer-texture shape-drawer-texture)
        (assoc :graphics/shape-drawer (sd/create batch (TextureRegion. shape-drawer-texture 1 0 1 1)))
        (assoc :graphics/textures (into {} (for [path (files-utils/search files texture-folder)]
                                             [path (Texture. ^String path)])))
        (assoc :graphics/unit-scale (atom 1)
               :graphics/world-unit-scale world-unit-scale)
        (assoc :graphics/tiled-map-renderer (tm-renderer/create world-unit-scale batch))
        (assoc :graphics/ui-viewport (fit-viewport/create (:width  ui-viewport)
                                                          (:height ui-viewport)
                                                          (orthographic-camera/create)))
        (assoc :graphics/world-viewport (let [world-width  (* (:width  world-viewport) world-unit-scale)
                                              world-height (* (:height world-viewport) world-unit-scale)]
                                          (fit-viewport/create world-width
                                                               world-height
                                                               (doto (orthographic-camera/create)
                                                                 (orthographic-camera/set-to-ortho! false world-width world-height)))))
        (assoc :graphics/draw-fns (update-vals draw-fns requiring-resolve)))))
