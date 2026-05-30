(ns game.ctx
  (:require [clojure.core-ext :refer [actions!
                                      reduce-actions!]]
            [clojure.math.vector2 :as v]
            [clojure.string :as str]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [com.badlogic.gdx.input.keys :as input.keys]
            [com.badlogic.gdx.utils.align :as align]
            [game.reaction-txs :as reaction-txs]
            [gdx.app :as app]
            [gdx.graphics.orthographic-camera :as camera]
            [malli.core :as m]
            [malli.utils :as mu]
            [space.earlygrey.shape-drawer :as shape-drawer]))

(def schema
  (m/schema
   [:map {:closed true}
    [:ctx/app :some] ; audio, files, graphics, input
    [:ctx/audio :some] ; sounds

    ; graphics?
    [:ctx/batch :some]
    [:ctx/cursors :some]
    [:ctx/default-font :some]
    [:ctx/unit-scale :some]
    [:ctx/world-unit-scale :some]
    [:ctx/world-viewport :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/textures :some]

    ; user interface
    [:ctx/skin :some]
    [:ctx/stage :some]

    ; generated each frame from other keys
    [:ctx/active-entities :any]
    [:ctx/delta-time :any]
    [:ctx/mouseover-eid :any]
    [:ctx/ui-mouse-position :any]
    [:ctx/world-mouse-position :any]

    ;; constants
    [:ctx/colors :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/max-delta :some]
    [:ctx/max-speed :some]
    [:ctx/minimum-size :some]
    [:ctx/render-z-order :some]
    [:ctx/z-orders :some]

    ;; game world
    [:ctx/content-grid :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some] ; constant?
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/start-position :some]
    [:ctx/tiled-map :some]
    [:ctx/db :some] ; also used @ editor
    [:ctx/elapsed-time :some]
    [:ctx/paused? :some] ; outside of game world ...
    [:ctx/player-eid :some] ; just a 'link'
    ]))

(defn validate [ctx]
  (mu/validate-humanize schema ctx))

(defn world-viewport-width
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-width world-viewport))

(defn world-viewport-height
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-height world-viewport))

(def zoom-speed 0.025)

(defn visible-tiles [{:keys [ctx/world-viewport]}]
  (camera/visible-tiles (:viewport/camera world-viewport)))

(def pausing? true)

(declare txs-fn-map)

(defn do! [ctx txs]
  (let [handled-txs (try (actions! txs-fn-map ctx txs)
                         (catch Throwable t
                           (throw (ex-info "Error handling txs"
                                           {:txs txs} t))))]
    (reduce-actions! reaction-txs/fn-map
                     ctx
                     handled-txs)))

(declare draw!)

(def draw-fns
  {
   :draw/circle           (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/circle! shape-drawer x y radius))
   :draw/ellipse          (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))
   :draw/filled-circle    (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-circle! shape-drawer x y radius))
   :draw/filled-rectangle (fn
                            [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-rectangle! shape-drawer x y w h))
   :draw/grid             (fn
                            [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
                            (let [w (* (float gridw) (float cellw))
                                  h (* (float gridh) (float cellh))
                                  topy (+ (float bottomy) (float h))
                                  rightx (+ (float leftx) (float w))]
                              (doseq [idx (range (inc (float gridw)))
                                      :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
                                (draw! ctx
                                       [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
                              (doseq [idx (range (inc (float gridh)))
                                      :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                                (draw! ctx
                                       [[:draw/line [leftx liney] [rightx liney] color-float-bits]]))))
   :draw/line             (fn
                            [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/line! shape-drawer sx sy ex ey))
   :draw/rectangle        (fn
                            [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/rectangle! shape-drawer x y w h))
   :draw/sector           (fn
                            [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))
   :draw/text             (fn
                            [{:keys [ctx/batch
                                     ctx/unit-scale
                                     ctx/default-font]}
                             {:keys [font scale x y text h-align up?]}]
                            (let [font (or font default-font)
                                  old-scale (font.data/scale-x (font/data font))
                                  target-width 0
                                  wrap? false
                                  scale (* (float @unit-scale)
                                           (float (or scale 1)))]
                              (font.data/set-scale! (font/data font) (* old-scale scale))
                              (font/draw! font
                                          batch
                                          text
                                          x
                                          (+ y (if up?
                                                 (-> text
                                                     (str/split #"\n")
                                                     count
                                                     (* (font/line-height font)))
                                                 0))
                                          target-width
                                          (align/k->value :align/center)
                                          wrap?)
                              (font.data/set-scale! (font/data font) old-scale)))
   :draw/texture-region   (fn
                            [{:keys [ctx/batch
                                     ctx/unit-scale
                                     ctx/world-unit-scale]}
                             texture-region
                             [x y]
                             & {:keys [center? rotation]}]
                            (let [[w h] (let [dimensions [(.getRegionWidth  texture-region)
                                                          (.getRegionHeight texture-region)]]
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
   :draw/with-line-width  (fn
                            [{:keys [ctx/shape-drawer]
                              :as ctx}
                             width
                             draws]
                            (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
                              (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
                              (draw! ctx draws)
                              (shape-drawer/set-default-line-width! shape-drawer old-line-width)))
   })

(defn draw! [ctx draws]
  (doseq [{k 0 :as component} draws
          :when component]
    (apply (get draw-fns k) ctx (rest component))))

(defn key-pressed? [{:keys [ctx/app]} input-key]
  (app/key-pressed? app input-key))

(defn world-unit-scale [ctx]
  (:ctx/world-unit-scale ctx))

(defn mouse-position [{:keys [ctx/app]}]
  (app/mouse-position app))

(defn button-just-pressed? [{:keys [ctx/app]} input-button]
  (app/button-just-pressed? app input-button))

; It is possible to put items out of sight, losing them.
; Because line of sight checks center of entity only, not corners
; this is okay, you have thrown the item over a hill, thats possible.
(defn item-place-position [{:keys [ctx/world-mouse-position]} player-entity]
  (let [player-position (:body/position (:entity/body player-entity))
        ; so you cannot put it out of your own reach
        maxrange (- (:entity/click-distance-tiles player-entity) 0.1)]
    (v/add player-position
           (v/scale (v/direction player-position world-mouse-position)
                    (min maxrange
                         (v/distance player-position world-mouse-position))))))

(defn sound-names [{:keys [ctx/audio]}]
  (map first audio))

(defn key-just-pressed? [{:keys [ctx/app]} input-key]
  (app/key-just-pressed? app input-key))

(defn player-movement-vector [ctx]
  (let [r (when (key-pressed? ctx input.keys/d) [1  0])
        l (when (key-pressed? ctx input.keys/a) [-1 0])
        u (when (key-pressed? ctx input.keys/w) [0  1])
        d (when (key-pressed? ctx input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v/normalise (reduce v/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v/length v))
          v)))))
