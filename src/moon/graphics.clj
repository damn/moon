(ns moon.graphics
  (:require [clojure.string :as str]
            [clojure.math :as math]
            [moon.viewport :as viewport]
            [moon.color :as color]
            [moon.files :as files-utils]
            [moon.graphics.camera :as camera]
            [moon.shape-drawer :as sd]
            [moon.tm-renderer :as tm-renderer])
  (:import (com.badlogic.gdx Gdx
                             Graphics)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color
                                      Colors
                                      Pixmap
                                      Pixmap$Format
                                      Texture
                                      Texture$TextureFilter
                                      OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d Batch
                                          BitmapFont
                                          SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.utils Align
                                   ScreenUtils)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defprotocol PGraphics
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

(defn- draw-text! [^BitmapFont font batch {:keys [scale text x y up? h-align target-width wrap?]}]
  (let [text-height (fn []
                      (-> text
                          (str/split #"\n")
                          count
                          (* (.getLineHeight font))))
        old-scale (.scaleX (.getData font))]
    (.setScale (.getData font) (* old-scale scale))
    (.draw font
           batch
           text
           (float x)
           (float (+ y (if up? (text-height) 0)))
           (float target-width)
           (or h-align Align/center)
           wrap?)
    (.setScale (.getData font) old-scale)))

(defmulti ^:private handle-draw!
  (fn [k graphics & params]
    k))

(defmethod handle-draw! :draw/with-line-width
  [_
   {:keys [graphics/shape-drawer]
    :as graphics}
   width
   draws]
  (sd/with-line-width shape-drawer width
    (draw! graphics draws)))

(defmethod handle-draw! :draw/grid
  [_ graphics leftx bottomy gridw gridh cellw cellh color]
  (let [w (* (float gridw) (float cellw))
        h (* (float gridh) (float cellh))
        topy (+ (float bottomy) (float h))
        rightx (+ (float leftx) (float w))]
    (doseq [idx (range (inc (float gridw)))
            :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
      (draw! graphics
                           [[:draw/line [linex topy] [linex bottomy] color]]))
    (doseq [idx (range (inc (float gridh)))
            :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
      (draw! graphics
                           [[:draw/line [leftx liney] [rightx liney] color]]))))

(defmethod handle-draw! :draw/texture-region
  [_
   {:keys [^Batch graphics/batch
           graphics/unit-scale
           graphics/world-unit-scale]}
   ^TextureRegion texture-region
   [x y]
   & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(.getRegionWidth  texture-region)
                                (.getRegionHeight texture-region)]]
                (if (= @unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * world-unit-scale))
                        dimensions)))]
    (if center?
      (.draw batch
             texture-region
             (- (float x) (/ (float w) 2))
             (- (float y) (/ (float h) 2))
             (/ (float w) 2)
             (/ (float h) 2)
             w
             h
             1
             1
             (or rotation 0))
      (.draw batch
             texture-region
             (float x)
             (float y)
             (float w)
             (float h)))))

(defmethod handle-draw! :draw/text
  [_
   {:keys [graphics/batch
           graphics/unit-scale
           graphics/default-font]}
   {:keys [font scale x y text h-align up?]}]
  (draw-text! (or font default-font)
              batch
              {:scale (* (float @unit-scale)
                         (float (or scale 1)))
               :text text
               :x x
               :y y
               :up? up?
               :h-align h-align
               :target-width 0
               :wrap? false}))

(defmethod handle-draw! :draw/ellipse
  [_
   {:keys [graphics/shape-drawer]}
   [x y] radius-x radius-y color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/ellipse! shape-drawer x y radius-x radius-y))

(defmethod handle-draw! :draw/filled-ellipse
  [_
   {:keys [graphics/shape-drawer]}
   [x y] radius-x radius-y color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/filled-ellipse! shape-drawer x y radius-x radius-y))

(defmethod handle-draw! :draw/circle
  [_
   {:keys [graphics/shape-drawer]}
   [x y] radius color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/circle! shape-drawer x y radius))

(defmethod handle-draw! :draw/filled-circle
  [_
   {:keys [graphics/shape-drawer]}
   [x y] radius color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/filled-circle! shape-drawer x y radius))

(defmethod handle-draw! :draw/rectangle
  [_
   {:keys [graphics/shape-drawer]}
   x y w h color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/rectangle! shape-drawer x y w h))

(defmethod handle-draw! :draw/filled-rectangle
  [_
   {:keys [graphics/shape-drawer]}
   x y w h color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/filled-rectangle! shape-drawer x y w h))

(defmethod handle-draw! :draw/arc
  [_
   {:keys [graphics/shape-drawer]}
   [center-x center-y] radius start-angle degree color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/arc! shape-drawer
           center-x
           center-y
           radius
           (math/to-radians start-angle)
           (math/to-radians degree)))

(defmethod handle-draw! :draw/sector
  [_
   {:keys [graphics/shape-drawer]}
   [center-x center-y] radius start-angle degree color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/sector! shape-drawer
              center-x
              center-y
              radius
              (math/to-radians start-angle)
              (math/to-radians degree)))

(defmethod handle-draw! :draw/line
  [_
   {:keys [graphics/shape-drawer]}
   [sx sy] [ex ey] color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/line! shape-drawer sx sy ex ey))

(defrecord RGraphics []
  PGraphics
  (unproject-ui [{:keys [graphics/ui-viewport]} position]
    (viewport/unproject ui-viewport position))

  (update-ui-viewport! [{:keys [graphics/ui-viewport]} width height]
    (viewport/update! ui-viewport width height {:center? true}))

  (dispose!
    [{:keys [graphics/batch
             graphics/cursors
             graphics/default-font
             graphics/shape-drawer-texture
             graphics/textures]}]
    (Disposable/.dispose batch)
    (run! Disposable/.dispose (vals cursors))
    (Disposable/.dispose default-font)
    (Disposable/.dispose shape-drawer-texture)
    (run! Disposable/.dispose (vals textures)))

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
      (apply handle-draw! k graphics (rest component))))

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
    (viewport/unproject world-viewport position))

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
        (assoc :graphics/ui-viewport (viewport/create (:width  ui-viewport)
                                                      (:height ui-viewport)
                                                      (OrthographicCamera.)))
        (assoc :graphics/world-viewport (let [world-width  (* (:width  world-viewport) world-unit-scale)
                                              world-height (* (:height world-viewport) world-unit-scale)]
                                          (viewport/create world-width
                                                           world-height
                                                           (doto (OrthographicCamera.)
                                                             (.setToOrtho false world-width world-height))))))))
