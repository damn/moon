(ns moon.graphics.impl
  (:require [clojure.math :as math]
            [clojure.string :as str]
            [moon.files :as files-utils]
            [moon.graphics]
            [moon.graphics.camera :as camera]
            [moon.graphics.color :as color]
            [moon.graphics.colors :as colors]
            [moon.graphics.g2d.batch :as batch]
            [moon.graphics.g2d.bitmap-font :as fnt]
            [moon.graphics.g2d.bitmap-font.data :as data]
            [moon.graphics.g2d.freetype.generator :as generator]
            [moon.graphics.g2d.freetype.generator.parameter :as parameter]
            [moon.graphics.g2d.shape-drawer :as sd]
            [moon.graphics.g2d.sprite-batch :as sprite-batch]
            [moon.graphics.g2d.texture-region :as texture-region]
            [moon.graphics.orthographic-camera :as orthographic-camera]
            [moon.graphics.pixmap :as pixmap]
            [moon.graphics.pixmap.format :as pixmap.format]
            [moon.graphics.texture :as texture]
            [moon.graphics.texture.filter :as texture.filter]
            [moon.graphics.tm-renderer :as tm-renderer]
            [moon.math.vector2 :as vector2]
            [moon.utils.align :as align]
            [moon.utils.disposable :as disposable]
            [moon.utils.screen :as screen-utils]
            [moon.utils.viewport :as viewport]
            [moon.utils.viewport.fit-viewport :as fit-viewport])
  (:import (com.badlogic.gdx Files
                             Graphics)))

(defn- draw-text! [font batch {:keys [scale text x y up? h-align target-width wrap?]}]
  (let [text-height (fn []
                      (-> text
                          (str/split #"\n")
                          count
                          (* (fnt/line-height font))))
        old-scale (data/scale-x (fnt/data font))]
    (data/set-scale! (fnt/data font) (* old-scale scale))
    (fnt/draw! font
               batch
               text
               x
               (+ y (if up? (text-height) 0))
               target-width
               (or h-align align/center)
               wrap?)
    (data/set-scale! (fnt/data font) old-scale)))

(defn- unproject [viewport [x y]]
  (-> viewport
      (viewport/unproject (vector2/->java x y))
      vector2/->clj))

(def ^:private draw-fns
  {
   :draw/with-line-width  (fn [{:keys [graphics/shape-drawer]
                                :as graphics}
                               width
                               draws]
                            (sd/with-line-width shape-drawer width
                              (moon.graphics/draw! graphics draws)))

   :draw/grid             (fn [graphics
                               leftx bottomy gridw gridh cellw cellh color]
                            (let [w (* (float gridw) (float cellw))
                                  h (* (float gridh) (float cellh))
                                  topy (+ (float bottomy) (float h))
                                  rightx (+ (float leftx) (float w))]
                              (doseq [idx (range (inc (float gridw)))
                                      :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
                                (moon.graphics/draw! graphics
                                                    [[:draw/line [linex topy] [linex bottomy] color]]))
                              (doseq [idx (range (inc (float gridh)))
                                      :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                                (moon.graphics/draw! graphics
                                                    [[:draw/line [leftx liney] [rightx liney] color]]))))

   :draw/texture-region   (fn [{:keys [graphics/batch
                                       graphics/unit-scale
                                       graphics/world-unit-scale]}
                               texture-region
                               [x y]
                               & {:keys [center? rotation]}]
                            (let [[w h] (let [dimensions (texture-region/dimensions texture-region)]
                                          (if (= @unit-scale 1)
                                            dimensions
                                            (mapv (comp float (partial * world-unit-scale))
                                                  dimensions)))]
                              (if center?
                                (batch/draw! batch
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
                                (batch/draw! batch texture-region x y w h))))

   :draw/text             (fn [{:keys [graphics/batch
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

   :draw/ellipse          (fn [{:keys [graphics/shape-drawer]}
                               [x y] radius-x radius-y color]
                            (sd/set-color! shape-drawer (color/float-bits color))
                            (sd/ellipse! shape-drawer x y radius-x radius-y))

   :draw/filled-ellipse   (fn [{:keys [graphics/shape-drawer]}
                               [x y] radius-x radius-y color]
                            (sd/set-color! shape-drawer (color/float-bits color))
                            (sd/filled-ellipse! shape-drawer x y radius-x radius-y))

   :draw/circle           (fn [{:keys [graphics/shape-drawer]}
                               [x y] radius color]
                            (sd/set-color! shape-drawer (color/float-bits color))
                            (sd/circle! shape-drawer x y radius))

   :draw/filled-circle    (fn [{:keys [graphics/shape-drawer]}
                               [x y] radius color]
                            (sd/set-color! shape-drawer (color/float-bits color))
                            (sd/filled-circle! shape-drawer x y radius))

   :draw/rectangle        (fn [{:keys [graphics/shape-drawer]}
                               x y w h color]
                            (sd/set-color! shape-drawer (color/float-bits color))
                            (sd/rectangle! shape-drawer x y w h))

   :draw/filled-rectangle (fn [{:keys [graphics/shape-drawer]}
                               x y w h color]
                            (sd/set-color! shape-drawer (color/float-bits color))
                            (sd/filled-rectangle! shape-drawer x y w h))

   :draw/arc              (fn [{:keys [graphics/shape-drawer]}
                               [center-x center-y] radius start-angle degree color]
                            (sd/set-color! shape-drawer (color/float-bits color))
                            (sd/arc! shape-drawer
                                     center-x
                                     center-y
                                     radius
                                     (math/to-radians start-angle)
                                     (math/to-radians degree)))

   :draw/sector           (fn [{:keys [graphics/shape-drawer]}
                               [center-x center-y] radius start-angle degree color]
                            (sd/set-color! shape-drawer (color/float-bits color))
                            (sd/sector! shape-drawer
                                        center-x
                                        center-y
                                        radius
                                        (math/to-radians start-angle)
                                        (math/to-radians degree)))

   :draw/line             (fn [{:keys [graphics/shape-drawer]}
                               [sx sy] [ex ey] color]
                            (sd/set-color! shape-drawer (color/float-bits color))
                            (sd/line! shape-drawer sx sy ex ey))
   }
  )

(defrecord RGraphics []
  moon.graphics/Graphics
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
    (Graphics/.getFramesPerSecond core))

  (delta-time [{:keys [graphics/core]}]
    (Graphics/.getDeltaTime core))

  (clear-screen! [_ color]
    (screen-utils/clear! color))

  (set-cursor! [{:keys [graphics/core
                        graphics/cursors]}
                cursor-key]
    (assert (contains? cursors cursor-key))
    (Graphics/.setCursor core (get cursors cursor-key)))

  (draw! [graphics draws]
    (doseq [{k 0 :as component} draws
            :when component]
      (apply (draw-fns k) graphics (rest component))))

  (texture-region [{:keys [graphics/textures]}
                   {:keys [image/file image/bounds]}]
    (assert file)
    (assert (contains? textures file))
    (let [texture (get textures file)]
      (if bounds
        (texture-region/create texture bounds)
        (texture-region/create texture))))

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
    [{:keys [graphics/batch
             graphics/shape-drawer
             graphics/unit-scale
             graphics/world-unit-scale
             graphics/world-viewport]}
     f]
    ; fix scene2d.ui.tooltip flickering
    ; _everything_ flickers with vis ui tooltip! it changes batch color somehow and does not
    ; change it back !
    (batch/set-color! batch [1 1 1 1])
    ;
    (batch/set-projection-matrix! batch (camera/combined (viewport/camera world-viewport)))
    (batch/begin! batch)
    (sd/with-line-width shape-drawer world-unit-scale
      (reset! unit-scale world-unit-scale)
      (f)
      (reset! unit-scale 1))
    (batch/end! batch)))

(defn- create-cursor [files graphics path [hotspot-x hotspot-y]]
  (let [pixmap (pixmap/create (Files/.internal files path))
        cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
    (pixmap/dispose! pixmap)
    cursor))

(defn- generate-font
  [file-handle {:keys [size
                       quality-scaling
                       enable-markup?
                       use-integer-positions?]}]
  (let [generator (generator/create file-handle)
        font (generator/font generator
                             (parameter/create
                              {:size (* size quality-scaling)
                               :min-filter texture.filter/linear
                               :mag-filter texture.filter/linear}))]
    (generator/dispose! generator)
    (data/set-scale! (fnt/data font) (/ quality-scaling))
    (data/set-enable-markup! (fnt/data font) enable-markup?)
    (fnt/set-use-integer-positions! font use-integer-positions?)
    font))

(defn create!
  [app
   {:keys [colors
           cursors
           default-font
           texture-folder
           tile-size
           ui-viewport
           world-viewport]}]
  (doseq [[name rgba] colors]
    (colors/put! name (color/create rgba)))
  (let [graphics (.getGraphics app)
        files    (.getFiles    app)
        batch (sprite-batch/create)
        shape-drawer-texture (let [pixmap (doto (pixmap/create 1 1 pixmap.format/rgba8888)
                                            (pixmap/set-color! [1 1 1 1])
                                            (pixmap/draw-pixel! 0 0))
                                   texture (texture/create pixmap)]
                               (pixmap/dispose! pixmap)
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
        (assoc :graphics/default-font (generate-font (Files/.internal files (:path default-font))
                                                     (:params default-font)))
        (assoc :graphics/batch batch)
        (assoc :graphics/shape-drawer-texture shape-drawer-texture)
        (assoc :graphics/shape-drawer (sd/create batch (texture-region/create shape-drawer-texture 1 0 1 1)))
        (assoc :graphics/textures (into {} (for [path (files-utils/search files texture-folder)]
                                             [path (texture/create path)])))
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
                                                                 (orthographic-camera/set-to-ortho! false world-width world-height))))))))
