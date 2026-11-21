(ns game
  (:require
   [clj-commons.pretty.repl :as pretty-repl]
   [clojure.com.badlogic.gdx.application-listener :as application-listener]
   [clojure.com.badlogic.gdx.audio :as audio]
   [clojure.com.badlogic.gdx.audio.sound :as sound]
   [clojure.com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
   [clojure.com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-application-configuration]
   [clojure.com.badlogic.gdx.files :as files]
   [clojure.com.badlogic.gdx.files.file-handle :as file-handle]
   [clojure.com.badlogic.gdx.gdx :as gdx]
   [clojure.com.badlogic.gdx.graphics :as graphics]
   [clojure.com.badlogic.gdx.graphics.color :as color]
   [clojure.com.badlogic.gdx.graphics.colors :as colors]
   [clojure.com.badlogic.gdx.graphics.g2d.batch :as batch]
   [clojure.com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
   [clojure.com.badlogic.gdx.graphics.g2d.bitmap-font-bitmap-font-data :as bitmap-font-data]
   [clojure.com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator :as free-type-font-generator]
   [clojure.com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator-free-type-font-parameter :as font-parameter]
   [clojure.com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
   [clojure.com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
   [clojure.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
   [clojure.com.badlogic.gdx.graphics.pixmap :as pixmap]
   [clojure.com.badlogic.gdx.graphics.pixmap-format :as pixmap-format]
   [clojure.com.badlogic.gdx.graphics.texture :as texture]
   [clojure.com.badlogic.gdx.graphics.texture-texture-filter :as texture-filter]
   [clojure.com.badlogic.gdx.maps.map-layers :as map-layers]
   [clojure.com.badlogic.gdx.maps.map-properties :as map-properties]
   [clojure.com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
   [clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
   [clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
   [clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer-cell :as tiled-map-tile-layer-cell]
   [clojure.com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
   [clojure.com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]
   [clojure.com.badlogic.gdx.math.circle :as circle]
   [clojure.com.badlogic.gdx.math.frustum :as frustum]
   [clojure.com.badlogic.gdx.math.intersector :as intersector]
   [clojure.com.badlogic.gdx.math.rectangle :as rectangle]
   [clojure.com.badlogic.gdx.math.vector2 :as vector2]
   [clojure.com.badlogic.gdx.math.vector3 :as vector3]
   [clojure.com.badlogic.gdx.scenes.scene2d.actor :as actor]
   [clojure.com.badlogic.gdx.scenes.scene2d.event :as event]
   [clojure.com.badlogic.gdx.scenes.scene2d.group :as group]
   [clojure.com.badlogic.gdx.scenes.scene2d.stage :as stage]
   [clojure.com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.button :as button]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.image :as image]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]
   [clojure.com.badlogic.gdx.scenes.scene2d.ui.window :as window]
   [clojure.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
   [clojure.com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]
   [clojure.com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
   [clojure.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
   [clojure.com.badlogic.gdx.utils.align :as align]
   [clojure.com.badlogic.gdx.utils.disposable :as disposable]
   [clojure.com.badlogic.gdx.utils.screen-utils :as screen-utils]
   [clojure.com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
   [clojure.com.badlogic.gdx.utils.viewport.viewport :as viewport]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.java.util.random :as random]
   [clojure.math :as math]
   [clojure.pprint :as pprint]
   [clojure.set :as set]
   [clojure.space.earlygrey.shapedrawer.shape-drawer :as shape-drawer]
   [clojure.string :as str]
   [game.input :as input]
   [malli.core :as m]
   [malli.error :as me]
   [reduce-fsm :as fsm])
  (:import
   (clojure RayCaster)))

(def float-rounding-error (double 0.000001))

(def transition-idxvalues [2 8 1 4])

(def slots #{:inventory.slot/bag
             :inventory.slot/weapon
             :inventory.slot/shield
             :inventory.slot/helm
             :inventory.slot/chest
             :inventory.slot/leg
             :inventory.slot/glove
             :inventory.slot/boot
             :inventory.slot/cloak
             :inventory.slot/necklace
             :inventory.slot/rings})

(def val-max-schema
  (m/schema
   [:and
    [:vector {:min 2 :max 2} [:int {:min 0}]]
    [:fn {:error/fn (fn [{[^int v ^int mx] :value} _]
                      (when (< mx v)
                        (format "Expected max (%d) to be smaller than val (%d)" v mx)))}
     (fn [[^int a ^int b]] (<= a b))]]))

(def k->adj-num
  {:wide
   (fn [open-paths random]
     (if (= open-paths 1)
       (case (int (* 3 (random/next-float random)))
         0 1
         2)
       (case (int (* 4 (random/next-float random)))
         0 1
         1 2
         2 3
         3 4
         1)))
   :thin
   ; höhle mit breite 1 überall nur -> turn-ratio verringern besser
   (fn [open-paths random]
     (if (= open-paths 1)
       1
       (case (int (* 7 (random/next-float random)))
         0 0
         1 2
         1)))
   :default
   ; etwas breiter als 1 aber immernoch zu dünn für m ein game -> turn-ratio verringern besser
   (fn [open-paths random]
     (if (= open-paths 1)
       (case (int (* 4 (random/next-float random)))
         0 1
         1 1
         2 1
         3 2
         1)
       (case (int (* 4 (random/next-float random)))
         0 0
         1 1
         2 1
         3 2
         1)))})

(defn safe-get [m k]
  {:pre [(contains? m k)]}
  (get m k))

(defn- sort-ops [ops]
  (sort-by (fn [[k]]
             (case k
               :op/inc 0
               :op/mult 1))
           ops))

(defn- apply-ops [ops base-value]
  (reduce (fn [base-value [k value]]
            (case k
              :op/inc  (+ base-value value)
              :op/mult (* base-value (inc (/ value 100)))))
          base-value
          (sort-ops ops)))

(defn mods-get-value [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (apply-ops (modifier-k modifiers)
             base-value))

(defn mods-format-text
  [mods]
  (when (seq mods)
    (str/join "\n"
              (keep (fn [[k modifier-ops]]
                      (str/join "\n"
                                (keep (fn [[op-k v]]
                                        (when-not (zero? v)
                                          (str (case (math/signum v)
                                                 0.0 ""
                                                 1.0 "+"
                                                 -1.0 "")
                                               (case op-k
                                                 :op/inc  (str v)
                                                 :op/mult (str v "%"))
                                               " "
                                               (str/capitalize (name k)))))
                                      (sort-ops modifier-ops))))
                    mods))))

(defn- valid-val-max? [val-max]
  (m/validate val-max-schema val-max))

(defn to-pos-int [val-max]
  (mapv #(-> % int (max 0)) val-max))

(defn val-max-ratio
  "If mx and v is 0, returns 0, otherwise (/ v mx)"
  [[^int v ^int mx]]
  {:pre [(valid-val-max? [v mx])]}
  (if (and (zero? v) (zero? mx))
    0
    (/ v mx)))

(defn apply-min [val-max modifiers modifier-k]
  (assert (valid-val-max? val-max) val-max)
  (let [val-max (update val-max 0 mods-get-value modifiers modifier-k)
        [v mx] (to-pos-int val-max)
        result [v (max v mx)]]
    (assert (valid-val-max? result) result)
    result))

(defn apply-max [val-max modifiers modifier-k]
  (assert (valid-val-max? val-max) val-max)
  (let [val-max (update val-max 1 mods-get-value modifiers modifier-k)
        [v mx] (to-pos-int val-max)
        result [(min v mx) mx]]
    (assert (valid-val-max? result) result)
    result))

(defn get-hitpoints
  [{:keys [stats/hp
           stats/modifiers]}]
  (apply-max hp modifiers :modifier/hp-max))

(defn get-mana
  [{:keys [stats/mana
           stats/modifiers]}]
  (apply-max mana modifiers :modifier/mana-max))

(defn get-value
  [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (mods-get-value base-value
                 (:stats/modifiers stats)
                 (keyword "modifier" (name stat-k)))))

(defn- add-ops [ops other-ops]
  (merge-with + ops other-ops))

(defn mods-add [mods other-mods]
  (merge-with add-ops mods other-mods))

(defn add-mods [stats mods]
  (update stats :stats/modifiers mods-add mods))

(defn- remove-ops [ops other-ops]
  (merge-with - ops other-ops))

(defn mods-remove [mods other-mods]
  (merge-with remove-ops mods other-mods))

(defn remove-mods [stats mods]
  (update stats :stats/modifiers mods-remove mods))

(defn calc-damage
  ([source target damage]
   (update (calc-damage source damage)
           :damage/min-max
           apply-max
           (:stats/modifiers target)
           :modifier/damage-receive-max))
  ([source damage]
   (update damage
           :damage/min-max
           #(-> %
                (apply-min (:stats/modifiers source) :modifier/damage-deal-min)
                (apply-max (:stats/modifiers source) :modifier/damage-deal-max)))))

(defn effective-armor-save [source-stats target-stats]
  (max (- (or (get-value source-stats :stats/armor-save) 0)
          (or (get-value target-stats :stats/armor-pierce) 0))
       0))

(defn melee-damage [{:keys [entity/stats]}]
  (let [strength (or (get-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))

(defn stats-format-text
  [stats]
  (str/join "\n" (concat
                   ["*STATS*"
                    (str "Mana: " (get-mana stats))
                    (str "Hitpoints: " (get-hitpoints stats))]
                   (for [stat-k [:stats/movement-speed
                                 :stats/aggro-range
                                 :stats/reaction-time
                                 :stats/strength
                                 :stats/cast-speed
                                 :stats/attack-speed
                                 :stats/armor-save
                                 :stats/armor-pierce]]
                     (str (str/capitalize (name stat-k)) ": "
                          (get-value stats stat-k))))))

(defn tile-movement-property
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (tiled-map-tile-layer/get-cell layer (int x) (int y))]
      (let [value (map-properties/get (tiled-map-tile/properties (tiled-map-tile-layer-cell/get-tile cell)) "movement")]
        (assert value
                (str "Value for :movement at position "
                     position " / mapeditor inverted position: " [(position 0)
                                                                 (- (dec (map-properties/get (tiled-map/properties tiled-map) "height"))
                                                                    (position 1))]
                     " and layer " (tiled-map-tile-layer/name layer) " is undefined."))
        value))))

(defn movement-property-layers [tiled-map]
  (->> (tiled-map/layers tiled-map)
       reverse
       (filter #(map-properties/get (tiled-map-tile-layer/properties %) "movement-properties"))))

(defn movement-properties [tiled-map position]
  (for [layer (movement-property-layers tiled-map)]
    [(tiled-map-tile-layer/name layer)
     (tile-movement-property tiled-map layer position)]))

(defn movement-property [tiled-map position]
  (or (->> tiled-map
           movement-property-layers
           (some #(tile-movement-property tiled-map % position)))
      "none"))

(defn prepare-creature-tiles [creature-properties image->texture-region]
  (for [{:keys [entity/animation
                creature/level
                property/id]} creature-properties
        :let [image (first (:animation/frames animation))
              texture-region (image->texture-region image)]]
    {:creature/level level
     :tile/id id
     :tile/texture-region texture-region}))

(defn tiled-map-tile-layer [width height tilewidth tileheight name visible?]
  (doto (tiled-map-tile-layer/create width height tilewidth tileheight)
    (tiled-map-tile-layer/set-name! name)
    (tiled-map-tile-layer/set-visible! visible?)))

(defn set-cell! [layer x y tile]
  (tiled-map-tile-layer/set-cell! layer
                                  x
                                  y
                                  (doto (tiled-map-tile-layer-cell/create)
                                    (tiled-map-tile-layer-cell/set-tile! tile))))

(defn create-static-tiled-map-tile
  [{:keys [tile/id
           tile/texture-region]}]
  (assert (and id
               texture-region))
  (let [tile (static-tiled-map-tile/create texture-region)]
    (map-properties/put! (tiled-map-tile/properties tile) "id" id)
    tile))

(defn add-creatures-layer! [tiled-map spawn-positions]
  (let [creature-tile (memoize create-static-tiled-map-tile)
        props (tiled-map/properties tiled-map)
        layer (tiled-map-tile-layer (map-properties/get props "width")
                                    (map-properties/get props "height")
                                    (map-properties/get props "tilewidth")
                                    (map-properties/get props "tileheight")
                                    "creatures"
                                    false)]
    (doseq [[[x y] tile] (for [[position creature-property] spawn-positions]
                           [position (creature-tile creature-property)])
            :when tile]
      (set-cell! layer x y tile))
    (map-layers/add! (tiled-map/layers tiled-map) layer)))

(defn- draw-tile!
  [x
   y
   tile
   unit-scale
   color-setter
   batch-color
   verts
   batch
   num-vertices]
  (let [region (tiled-map-tile/texture-region tile)
        x1 (+ x (* (tiled-map-tile/offset-x tile) unit-scale))
        y1 (+ y (* (tiled-map-tile/offset-y tile) unit-scale))
        x2 (+ x1 (* (texture-region/region-width region) unit-scale))
        y2 (+ y1 (* (texture-region/region-height region) unit-scale))
        u1 (texture-region/u region)
        v1 (texture-region/v2 region)
        u2 (texture-region/u2 region)
        v2 (texture-region/v region)
        color11 (float (color-setter batch-color x1 y1))
        color12 (float (color-setter batch-color x1 y2))
        color22 (float (color-setter batch-color x2 y2))
        color21 (float (color-setter batch-color x2 y1))]
    (aset-float verts batch/X1 x1)
    (aset-float verts batch/Y1 y1)
    (aset-float verts batch/C1 color11)
    (aset-float verts batch/U1 u1)
    (aset-float verts batch/V1 v1)
    (aset-float verts batch/X2 x1)
    (aset-float verts batch/Y2 y2)
    (aset-float verts batch/C2 color12)
    (aset-float verts batch/U2 u1)
    (aset-float verts batch/V2 v2)
    (aset-float verts batch/X3 x2)
    (aset-float verts batch/Y3 y2)
    (aset-float verts batch/C3 color22)
    (aset-float verts batch/U3 u2)
    (aset-float verts batch/V3 v2)
    (aset-float verts batch/X4 x2)
    (aset-float verts batch/Y4 y1)
    (aset-float verts batch/C4 color21)
    (aset-float verts batch/U4 u2)
    (aset-float verts batch/V4 v1)
    (batch/draw batch
                (texture-region/texture region)
                verts
                0
                num-vertices)))

(defn- draw-tile-layer!
  [layer
   batch
   view-bounds
   color-setter
   world-unit-scale]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (batch/color batch)
        layer-width (tiled-map-tile-layer/width layer)
        layer-height (tiled-map-tile-layer/height layer)
        layer-tile-width (* (tiled-map-tile-layer/tile-width layer) world-unit-scale)
        layer-tile-height (* (tiled-map-tile-layer/tile-height layer) world-unit-scale)
        layer-offset-x (* (tiled-map-tile-layer/render-offset-x layer) world-unit-scale)
        layer-offset-y (* (- (tiled-map-tile-layer/render-offset-y layer)) world-unit-scale)
        col1 (max 0
                  (int (/ (- (:x view-bounds) layer-offset-x)
                          layer-tile-width)))
        col2 (min layer-width
                  (int (/ (+ (:x view-bounds)
                             (:width view-bounds)
                             layer-tile-width
                             (- layer-offset-x))
                          layer-tile-width)))
        row1 (max 0
                  (int (/ (- (:y view-bounds) layer-offset-y)
                          layer-tile-height)))
        row2 (min layer-height
                  (int (/ (+ (:y view-bounds)
                             (:height view-bounds)
                             layer-tile-height
                             (- layer-offset-y))
                          layer-tile-height)))
        x-start (+ (* col1 layer-tile-width)
                   layer-offset-x)
        verts (aclone vertices)]
    (loop [row row2
           y (+ (* row2 layer-tile-height)
                layer-offset-y)]
      (when (>= row row1)
        (loop [col col1
               x x-start]
          (when (< col col2)
            (when-let [cell (tiled-map-tile-layer/get-cell layer col row)]
              (when-let [tile (tiled-map-tile-layer-cell/get-tile cell)]
                (draw-tile! x
                            y
                            tile
                            world-unit-scale
                            color-setter
                            batch-color
                            verts
                            batch
                            num-vertices)))
            (recur (inc col)
                   (+ x layer-tile-width))))
        (recur (dec row)
               (- y layer-tile-height))))))

(defn camera-view-bounds [camera]
  (let [width  (* (orthographic-camera/viewport-width camera) (orthographic-camera/zoom camera))
        height (* (orthographic-camera/viewport-height camera) (orthographic-camera/zoom camera))
        up (orthographic-camera/up camera)
        w (+ (* width  (Math/abs (float (vector3/y up))))
             (* height (Math/abs (float (vector3/x up)))))
        h (+ (* height (Math/abs (float (vector3/y up))))
             (* width  (Math/abs (float (vector3/x up)))))
        pos (orthographic-camera/position camera)]
    {:x (- (vector3/x pos) (/ w 2))
     :y (- (vector3/y pos) (/ h 2))
     :width w
     :height h}))

(defn draw-tiled-map!
  [tiled-map batch view-bounds color-setter world-unit-scale]
  (doseq [layer (filter tiled-map-tile-layer/visible? (tiled-map/layers tiled-map))]
    (draw-tile-layer! layer
                      batch
                      view-bounds
                      color-setter
                      world-unit-scale)))

(defn- recursively-search [handle extensions]
  (loop [[handle & remaining] (file-handle/list handle)
         result []]
    (cond (nil? handle)
          result

          (file-handle/directory? handle)
          (recur (concat remaining (file-handle/list handle)) result)

          (extensions (file-handle/extension handle))
          (recur remaining (conj result (file-handle/path handle)))

          :else
          (recur remaining result))))

(defn create-textures
  [internal {:keys [folder extensions]}]
  (into {}
        (for [path (->> (recursively-search (internal folder) extensions)
                        (map #(str/replace-first % folder "")))]
          [path (texture/create (internal path))])))

(defn get-texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (let [texture (safe-get textures file)]
    (if-let [[x y w h] bounds]
      (texture-region/create texture x y w h)
      (texture-region/create texture))))

(defn timer-increment [timer duration]
  (update timer :stop-time + duration))

(defn v2-scale [[x y] scalar]
  [(* x scalar)
   (* y scalar)])

(defn v2-dot
  [[this-x this-y]
   [x y]]
  (+ (* this-x x)
     (* this-y y)))

(defn v2-crs
  "Calculates the 2D cross product between this and the given vector"
  [[this-x this-y] [x y]]
  (- (* this-x y)
     (* this-y x)))

(defn v2-length [[x y]]
  (math/sqrt (+ (* x x)
                (* y y))))

(defn v2-normalise [[x y :as v]]
  (let [len (v2-length v)]
    (if (zero? len)
      v
      [(/ x len)
       (/ y len)])))

(defn v2-normal-vectors [[x y]]
  [[(- (float y))         x]
   [          y (- (float x))]])

(defn v2-direction [[sx sy] [tx ty]]
  (v2-normalise [(- (float tx) (float sx))
                 (- (float ty) (float sy))]))

(defn v2-distance
  [[x1 y1]
   [x2 y2]]
  (let [x-d (- x2 x1)
        y-d (- y2 y1)]
    (math/sqrt (+ (* x-d x-d)
                  (* y-d y-d)))))

(defmacro with-line-width [shape-drawer width & body]
  `(let [old-line-width# (shape-drawer/default-line-width ~shape-drawer)]
     (shape-drawer/set-default-line-width ~shape-drawer (* ~width old-line-width#))
     ~@body
     (shape-drawer/set-default-line-width ~shape-drawer old-line-width#)))

(defn filled-rectangle! [shape-drawer x y w h color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/filled-rectangle! shape-drawer x y w h))

(defn rectangle! [shape-drawer x y w h color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/rectangle! shape-drawer x y w h))

(defn line! [shape-drawer [sx sy] [ex ey] color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/line! shape-drawer sx sy ex ey))

(defn circle! [shape-drawer [x y] radius color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/circle! shape-drawer x y radius))

(defn ellipse! [shape-drawer [x y] radius-x radius-y color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))

(defn filled-circle! [shape-drawer [x y] radius color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/filled-circle! shape-drawer x y radius))

(defn sector! [shape-drawer [center-x center-y] radius start-radians radians color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))

(defn touched-tiles*
  "x is leftmost point and y bottom most point in the rectangle."
  [{:keys [x y width height]}]
  {:pre [x y width height]}
  (let [x       (float x)
        y       (float y)
        width   (float width)
        height  (float height)
        l (int x)
        b (int y)
        r (int (+ x width))
        t (int (+ y height))]
    (set
     (if (or (> width 1) (> height 1))
       (for [x (range l (inc r))
             y (range b (inc t))]
         [x y])
       [[l b] [l t] [r b] [r t]]))))

(defn raycaster-blocked?
  [[bool-arr
    int-width
    int-height]
   [start-x start-y]
   [target-x target-y]]
  (RayCaster/rayBlocked (double start-x)
                        (double start-y)
                        (double target-x)
                        (double target-y)
                        int-width
                        int-height
                        bool-arr))

(defn line-of-sight?
  [raycaster source target]
  (not (raycaster-blocked? raycaster
                           (:entity/position source)
                           (:entity/position target))))

(defn nearly-equal?
  ([x y]
   (nearly-equal? x y float-rounding-error))
  ([a b epsilon]
   (<= (Math/abs (double (- a b)))
       epsilon)))

(defn round-n [^double x n]
  (let [z (math/pow 10 n)]
    (float
     (/
      (math/round (* x z))
      z))))

(defn readable [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (nearly-equal? x (int x) 0.001))
    (int x)
    (round-n x 2)))

(let [offsets [[-1 -1] [-1 0] [-1 1] [0 -1] [0 1] [1 -1] [1 0] [1 1]]]
  (defn get-8-neighbours [position]
    (mapv #(mapv + position %) offsets)))

(defn get-4-neighbours [[x y]]
  [[(inc x) y]
   [(dec x) y]
   [x (inc y)]
   [x (dec y)]])

(defn diagonal? [[x1 y1] [x2 y2]]
  (and (not= x1 x2)
       (not= y1 y2)))

(defn- set-label-text-actor [label-widget text-fn]
  (actor/create
    {:act!
     (fn [this _delta]
       (when (actor/stage this)
         (label/set-text! label-widget (text-fn))))
     :draw!
     (fn [_this _batch _parent-alpha])}))

(defn- add-upd-label!
  ([skin tbl text-fn icon]
   (let [lbl (label/create "" skin)
         sub-table (table/create)]
     (doseq [c [{:actor (image/create-from-texture icon)}
                {:actor lbl}]]
       (table/add! sub-table (:actor c)))
     (table/row! sub-table)
     (layout/pack! sub-table)
     (group/add-actor! tbl (set-label-text-actor lbl text-fn))
     (doto (table/add! tbl sub-table)
       (cell/right!)
       (cell/expand-x!))))
  ([skin tbl text-fn]
   (let [lbl (label/create "" skin)]
     (group/add-actor! tbl (set-label-text-actor lbl text-fn))
     (doto (table/add! tbl lbl)
       (cell/right!)
       (cell/expand-x!)))))

(defn- main-table [skin menus update-labels]
  (let [table (table/create)]
    (doseq [cell (for [{:keys [label items]} menus]
                   {:actor
                    (doto (text-button/create label skin)
                      (actor/add-listener! (change-listener/create
                                       (fn [event actor]
                                         (let [stage (event/stage event)]
                                           (stage/add-actor! stage
                                                      (let [window (window/create label skin)]
                                                        (doseq [{:keys [label on-click]} items]
                                                          (table/add! window (doto (text-button/create label skin)
                                                                         (actor/add-listener! (change-listener/create
                                                                                          (fn [_event _actor]
                                                                                            (on-click)))))))
                                                        (table/row! window)
                                                        (layout/pack! window)
                                                        (table/add! (window/title-table window)
                                                              (doto (text-button/create "X" skin)
                                                                (actor/add-listener! (change-listener/create
                                                                                 (fn [_event _actor]
                                                                                   (actor/remove! window))))))
                                                        window)))))))})]
      (table/add! table (:actor cell)))
    (table/row! table)
    (layout/pack! table)
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create-dev-menu
  [{:keys [menus update-labels skin]}]
  (let [table (table/create)]
    (doto (table/add! table (main-table skin menus update-labels))
      (cell/expand-x!)
      (cell/fill-x!)
      (cell/colspan! 1))
    (table/row! table)
    (doto (table/add! table (doto (label/create "" skin)
                        (actor/set-touchable! touchable/disabled)))
      (cell/expand!)
      (cell/fill-x!)
      (cell/fill-y!))
    (table/row! table)
    (doto table
      (layout/pack!)
      (table/set-fill-parent! true))))

(defn- nad-corner? [grid [fromx fromy] [tox toy]]
  (and
   (= :ground (get-in grid [tox toy])) ; also filters nil/out of map
   (= :wall (get-in grid [tox fromy]))
   (= :wall (get-in grid [fromx toy]))))

; could be made faster because accessing the same posis oftentimes at nad-corner? check
(let [diagonal-steps [[-1 -1] [-1 1] [1 -1] [1 1]]]
  (defn- get-nads [grid]
    (loop [checkposis (filter (fn [{y 1 :as posi}]
                                (and (even? y)
                                     (= :ground (get-in grid posi))))
                              (for [x (range (count grid))
                                    y (range (count (grid 0)))]
                                [x y]))
           result []]
      (if (seq checkposis)
        (let [position (first checkposis)
              diagonal-posis (map #(mapv + position %) diagonal-steps)
              nads (map (fn [nad] [position nad])
                        (filter #(nad-corner? grid position %) diagonal-posis))]
          (recur
           (rest checkposis)
           (doall (concat result nads)))) ; doall else stackoverflow error
        result))))

(defn- get-tiles-needing-fix-for-nad [grid [[fromx fromy] [tox toy]]]
  (let [xstep (- tox fromx)
        ystep (- toy fromy)
        cell1x (+ fromx xstep)
        cell1y fromy
        cell1 [cell1x cell1y]
        cell11 [(+ cell1x xstep) (+ cell1y (- ystep))]
        cell2x (+ cell1x xstep)
        cell2y cell1y
        cell2 [cell2x cell2y]
        cell21 [(+ cell2x xstep) (+ cell2y ystep)]
        cell3 [cell2x (+ cell2y ystep)]]
    ;    (println "from: " [fromx fromy] " to: " [tox toy])
    ;    (println "xstep " xstep " ystep " ystep)
    ;    (println "cell1 " cell1)
    ;    (println "cell11 " cell11)
    ;    (println "cell2 " cell2)
    ;    (println "cell21 " cell21)
    ;    (println "cell3 " cell3)
    (if-not (nad-corner? grid cell1 cell11)
      [cell1]
      (if-not (nad-corner? grid cell2 cell21)
        [cell1 cell2]
        [cell1 cell2 cell3]))))

(defn fix-nads [grid]
  {:pre [(= #{:wall :ground} (set (apply concat grid)))]
   :post [(= #{:wall :ground} (set (apply concat %)))]}
  (reduce #(assoc-in %1 %2 :ground) grid
          (mapcat #(get-tiles-needing-fix-for-nad grid %)
                  (get-nads grid))))

(defn flood-fill [grid start walk-on-position?]
  (loop [next-positions [start]
         filled []
         grid grid]
    (if (seq next-positions)
      (recur (filter #(and (get-in grid %)
                           (walk-on-position? %))
                     (distinct
                      (mapcat get-8-neighbours
                              next-positions)))
             (concat filled next-positions)
             (reduce #(assoc-in %1 %2 nil) grid next-positions))
      filled)))

(defn- recur-sort [m]
  (into (sorted-map)
        (zipmap (keys m)
                (map #(if (map? %)
                        (recur-sort %)
                        %)
                     (vals m)))))

(defn save! [db file]
  (let [data (->> (vals db)
                  (sort-by #(keyword "properties" (namespace (:property/id %))))
                  (map recur-sort)
                  doall)]
    (.start
     (Thread.
      (fn []
        (binding [*print-level* nil]
          (->> data
               pprint/pprint
               with-out-str
               (spit file))))))))

(defn all-raw [db property-type]
  (->> (vals db)
       (filter #(= property-type (keyword "properties" (namespace (:property/id %)))))))

(defn unproject [vp [x y]]
  (let [v2 (vector2/create x y)]
    (viewport/unproject! vp v2)
    [(vector2/x v2) (vector2/y v2)]))

(defn inventory-window-cell-highlight-widget [cell draw-cell-rect! get-mouse-position]
  (widget/create
    {:draw!
     (fn [this _batch _parent-alpha]
       (let [stage-mouse-coords (unproject (stage/viewport (actor/stage this)) (get-mouse-position))
             local (let [v2 (actor/stage->local-coordinates this (vector2/create (stage-mouse-coords 0) (stage-mouse-coords 1)))]
                     [(vector2/x v2) (vector2/y v2)])]
         (draw-cell-rect! (actor/x this)
                          (actor/y this)
                          (actor/hit this (local 0) (local 1) true)
                          cell)))}))

(defn inventory-window-cell-image-widget [background-drawable cell-size]
  (doto (image/create-from-drawable background-drawable)
    (actor/set-name! "image-widget")
    (actor/set-user-object! {:background-drawable background-drawable
                       :cell-size cell-size})))

(defn add-actors! [g actors]
  (run! #(group/add-actor! g %) actors))

(defn- inv-window-cell [get-mouse-position on-click-cell slot->drawable draw-cell-rect! cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    (doto (stack/create)
      (add-actors! [(inventory-window-cell-highlight-widget cell draw-cell-rect! get-mouse-position)
                    (inventory-window-cell-image-widget background-drawable cell-size)])
      (actor/add-listener! (click-listener/create
                             (fn [event _x _y]
                               (on-click-cell event cell))))
      (actor/set-name! "inventory-cell")
      (actor/set-user-object! cell))))

(defn toggle-visible! [obj]
  (actor/set-visible! obj (not (actor/visible? obj))))

(defn- build-inventory-window
  [{:keys [get-mouse-position
           on-click-cell
           draw-cell-rect!
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (texture-region-drawable/create (slot->texture-region slot))
                           (texture-region-drawable/set-min-size! cell-size cell-size)
                           (texture-region-drawable/tint (color/create 1 1 1 0.4))))
        ->cell (partial inv-window-cell get-mouse-position on-click-cell slot->drawable draw-cell-rect! cell-size)
        cell-table (table/create)
        window (window/create "Inventory" skin)]
    (doseq [row (concat [[nil 
                          nil
                          (->cell :inventory.slot/helm)
                          (->cell :inventory.slot/necklace)]
                         [nil
                          (->cell :inventory.slot/weapon)
                          (->cell :inventory.slot/chest)
                          (->cell :inventory.slot/cloak)
                          (->cell :inventory.slot/shield)]
                         [nil 
                          nil
                          (->cell :inventory.slot/leg)]
                         [nil
                          (->cell :inventory.slot/glove)
                          (->cell :inventory.slot/rings :position [0 0])
                          (->cell :inventory.slot/rings :position [1 0])
                          (->cell :inventory.slot/boot)]]
                        (for [y (range 4)]
                          (for [x (range 6)]
                            (->cell :inventory.slot/bag :position [x y]))))]
      (doseq [actor row]
        (table/add! cell-table actor))
      (table/row! cell-table))
    (layout/pack! cell-table)
    (actor/set-name! cell-table "inventory-cell-table")
    (let [c (table/add! window cell-table)]
      (cell/pad! c 4))
    (table/row! window)
    (layout/pack! window)
    (actor/set-name! window "moon.ui.windows.inventory")
    (actor/set-visible! window false)
    (let [[x y] position]
      (actor/set-position! window x y))
    window))

(defn create-content-grid [width height cell-size]
  (let [w (inc (int (/ width cell-size)))
        h (inc (int (/ height cell-size)))
        xyfn (fn [idx]
               (atom {:idx idx
                      :entities #{}}))]
    {:grid (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                               (range h)))
                 (range w))
     :cell-w cell-size
     :cell-h cell-size}))

(defn update-entity!
  [{:keys [grid
           cell-w
           cell-h]}
   eid]
  (let [{:keys [entity/position]
         content-cell :moon.content-grid/content-cell} @eid
        [x y] position
        new-cell (get-in grid [(int (/ x cell-w))
                               (int (/ y cell-h))])]
    (when-not (= content-cell new-cell)
      (swap! new-cell update :entities conj eid)
      (swap! eid assoc :moon.content-grid/content-cell new-cell)
      (when content-cell
        (swap! content-cell update :entities disj eid)))))

(defn remove-entity! [_ eid]
  (-> @eid
      :moon.content-grid/content-cell
      (swap! update :entities disj eid)))

(defn active-entities*
  [{:keys [grid]} center-entity]
  (assert (map? center-entity))
  (->> (let [idx (-> center-entity
                     :moon.content-grid/content-cell
                     deref
                     :idx)]
         (cons idx (get-8-neighbours idx)))
       (keep #(get-in grid %))
       (mapcat (comp :entities deref))))

(defn- sshuffle
  "Return a random permutation of coll"
  [coll random]
  (let [al (java.util.ArrayList. ^java.util.Collection coll)]
    (java.util.Collections/shuffle al ^java.util.Random random)
    (clojure.lang.RT/vector (.toArray al))))

(defn- from-mapgrid
  "Transforms a grid of {position value} to a grid2d.
  Returns [grid convert-fn]: convert-fn converts a position of the old grid to a position of the new one."
  [grid calc-newgrid-value]
  (let [posis (keys grid)
        xs (map #(% 0) posis)
        min-x (apply min xs)
        max-x (apply max xs)
        ys (map #(% 1) posis)
        min-y (apply min ys)
        max-y (apply max ys)
        width (inc (- max-x min-x))
        height (inc (- max-y min-y))
        convert (fn [[x y]] [(- x min-x -1)
                             (- y min-y -1)])
        w (+ width 2)
        h (+ height 2)
        xyfn (fn [[x y]]
               ; new grid starts 1 left/top of leftest cell
               (calc-newgrid-value (get grid [(+ x min-x -1)
                                              (+ y min-y -1)])))]
    ; +2 so there are walls on all borders around the farthest ground cells
    [(mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                         (range h)))
           (range w))
     convert]))

;  Cave Algorithmus.
;  http://properundead.com/2009/03/cave-generator.html
;  http://properundead.com/2009/07/procedural-generation-3-cave-source.html
;  http://forums.tigsource.com/index.php?topic=5174.0
; gute ergebnisse: :wide / 500-4000 max-cells / turn-ratio 0.5
; besser 150x150 anstatt 100x100 w h
; TODO glaubich einziger unterschied noch: openpaths wird bei jeder cell neu berechnet?
; TODO max-tries wenn er nie über min-cells kommt? -> im let dazu definieren vlt max 30 sekunden -> in tries umgerechnet??
(defn generate-caves [random min-cells max-cells get-adj-num-fn]
  (let [create-order (fn [] (sshuffle (range 4) random))
        turn-ratio 0.25
        start [0 0]
        start-grid (assoc {} start :ground) ; grid of posis to :ground or no entry for walls
        finished (fn [grid end cell-cnt]
                   ;(println "Reached cells: " cell-cnt) ; TODO cell-cnt stimmt net genau
                   ; TODO already called there down ... make mincells check there
                   (if (< cell-cnt min-cells)
                     (generate-caves random min-cells max-cells get-adj-num-fn) ; recur?
                     (let [[grid convert] (from-mapgrid grid #(if (nil? %) :wall :ground))]
                       {:grid  grid
                        :start (convert start)
                        :end   (convert end)})))]
    (loop [posi-seq [start]
           grid     start-grid
           cell-cnt 0
           current-order (create-order)]
      ; TODO min cells check !?
      (if (>= cell-cnt max-cells)
        (finished grid
                  (last posi-seq)
                  cell-cnt)
        (let [current-order (if (< (random/next-float random) turn-ratio)
                              (create-order)
                              current-order)
              neighbours (get-4-neighbours (last posi-seq))
              try-carve-posis (take (get-adj-num-fn (count posi-seq) random)
                                    (map #(get neighbours %) current-order))
              carve-posis (filter #(nil? (get grid %)) try-carve-posis)
              new-pos-seq (concat (drop-last posi-seq) carve-posis)]
          (if (not-empty new-pos-seq)
            (recur new-pos-seq
                   (reduce #(assoc %1 %2 :ground) grid carve-posis)
                   (+ cell-cnt (count carve-posis))
                   current-order)
            ; TODO here min-cells check ?
            (finished grid (last posi-seq) cell-cnt)))))))

(defn create-caves [random min-cells max-cells adjnum-type]
  (generate-caves random min-cells max-cells (k->adj-num adjnum-type)))

(defn- module-index->tiled-map-positions
  [[module-x module-y]
   [modules-width modules-height]
   module-offset-tiles]
  (let [start-x (* module-x (+ modules-width module-offset-tiles))
        start-y (* module-y (+ modules-height module-offset-tiles))]
    (for [x (range start-x (+ start-x modules-width))
          y (range start-y (+ start-y modules-height))]
      [x y])))

(defn- transition-idx-value [position position->transition?]
  (->> position
       get-4-neighbours
       (map-indexed (fn [idx position]
                      (if (position->transition? position)
                        (transition-idxvalues idx)
                        0)))
       (apply +)))

(defn- place-module*
  [module-offset-tiles
   modules-scale
   scaled-grid
   unscaled-position
   & {:keys [transition?
             transition-neighbor?]}]
  (let [floor-modules-row-width 4
        floor-modules-row-height 4
        floor->module-index (fn []
                              [(rand-int floor-modules-row-width)
                               (rand-int floor-modules-row-height)])
        transition-modules-row-width 4
        transition-modules-row-height 4
        transition-modules-offset-x 4
        transition-idxvalue->module-index (fn [idxvalue]
                                            [(+ (rem idxvalue transition-modules-row-width)
                                                transition-modules-offset-x)
                                             (int (/ idxvalue transition-modules-row-height))])
        [modules-width modules-height] modules-scale
        floor-idxvalue 0
        idxvalue (if transition?
                   (transition-idx-value unscaled-position transition-neighbor?)
                   floor-idxvalue)
        tiled-map-positions (module-index->tiled-map-positions
                             (if transition?
                               (transition-idxvalue->module-index idxvalue)
                               (floor->module-index))
                             modules-scale
                             module-offset-tiles)
        offsets (for [x (range modules-width)
                      y (range modules-height)]
                  [x y])
        offset->tiled-map-position (zipmap offsets tiled-map-positions)
        scaled-position (mapv * unscaled-position modules-scale)]
    (reduce (fn [grid offset]
              (assoc-in grid
                     (mapv + scaled-position offset)
                     (offset->tiled-map-position offset)))
            scaled-grid
            offsets)))

(defn- place-step
  [modules-tiled-map
   modules-scale
   scaled-grid
   unscaled-grid
   unscaled-floor-positions
   unscaled-transition-positions]
  (let [module-offset-tiles 1
        number-modules-x 8
        number-modules-y 4
        [modules-width modules-height] modules-scale
        props (tiled-map/properties modules-tiled-map)
        _ (assert (and (= (map-properties/get props "width")
                          (* number-modules-x (+ modules-width module-offset-tiles)))
                       (= (map-properties/get props "height")
                          (* number-modules-y (+ modules-height module-offset-tiles)))))
        scaled-grid (reduce (fn [scaled-grid unscaled-position]
                              (place-module* module-offset-tiles
                                             modules-scale
                                             scaled-grid
                                             unscaled-position
                                             :transition? false))
                            scaled-grid
                            unscaled-floor-positions)
        scaled-grid (reduce (fn [scaled-grid unscaled-position]
                              (place-module* module-offset-tiles
                                             modules-scale
                                             scaled-grid
                                             unscaled-position
                                             :transition? true
                                             :transition-neighbor? #(#{:transition :wall}
                                                                     (get-in unscaled-grid %))))
                            scaled-grid
                            unscaled-transition-positions)]
    scaled-grid))

(defn- grid->tiled-map
  [schema-tiled-map grid]
  (let [copy-tile (memoize static-tiled-map-tile/copy)]
    {:properties (merge (let [props (tiled-map/properties schema-tiled-map)]
                          (zipmap (map-properties/get-keys props)
                                  (map-properties/get-values props)))
                        {"width" (count grid)
                         "height" (count (grid 0))})
     :layers (for [layer (tiled-map/layers schema-tiled-map)]
               {:name (tiled-map-tile-layer/name layer)
                :visible? (tiled-map-tile-layer/visible? layer)
                :properties (let [props (tiled-map-tile-layer/properties layer)]
                              (zipmap (map-properties/get-keys props)
                                      (map-properties/get-values props)))
                :tiles (for [x (range (count grid))
                             y (range (count (grid 0)))
                             :let [position [x y]
                                   local-position (get-in grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (tiled-map-tile-layer/get-cell layer (local-position 0) (local-position 1))]
                             [position (copy-tile (tiled-map-tile-layer-cell/get-tile cell))])))})}))

(defn- create-tiled-map* [schema-tiled-map scaled-grid]
  (let [{:keys [properties layers]} (grid->tiled-map schema-tiled-map scaled-grid)
        tiled-map (tiled-map/create)]
    (doseq [[k v] properties]
      (assert (string? k))
      (map-properties/put! (tiled-map/properties tiled-map) k v))
    (doseq [{:keys [name visible? properties tiles]} layers]
      (assert (string? name))
      (assert (boolean? visible?))
      (let [props (tiled-map/properties tiled-map)
            layer (tiled-map-tile-layer (map-properties/get props "width")
                                        (map-properties/get props "height")
                                        (map-properties/get props "tilewidth")
                                        (map-properties/get props "tileheight")
                                        name
                                        visible?)]
        (doseq [[k v] properties]
          (assert (string? k))
          (map-properties/put! (tiled-map-tile-layer/properties layer) k v))
        (doseq [[[x y] tile] tiles
                :when tile]
          (set-cell! layer x y tile))
        (map-layers/add! (tiled-map/layers tiled-map) layer)))
    tiled-map))

(defn create-modules-level
  [{:keys [level/creature-properties
           world/map-size
           world/spawn-rate]
    :or {map-size 5
         spawn-rate 0.05}}]
  (let [scale [32 20]
        {:keys [start grid]} (create-caves (random/create) map-size map-size :wide)
        grid (fix-nads grid)
        grid (let [grid (reduce #(assoc-in %1 %2 :transition)
                                grid
                                (filter (fn [position]
                                          (and (= :wall (get-in grid position))
                                               (some #(= :ground (get-in grid %))
                                                     (get-8-neighbours position))))
                                        (for [x (range (count grid)) y (range (count (grid 0)))] [x y])))]
               (assert (or
                        (= #{:wall :ground :transition} (set (apply concat grid)))
                        (= #{:ground :transition} (set (apply concat grid))))
                       (str "(set (apply concat grid)): " (set (apply concat grid))))
               grid)
        scaled-grid (let [[sw sh] scale
                          w (* (count grid) sw)
                          h (* (count (grid 0)) sh)
                          xyfn (fn [[x y]]
                                 (get-in grid
                                         [(int (/ x sw))
                                          (int (/ y sh))]))]
                      (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                                          (range h)))
                            (range w)))
        schema-tiled-map (tmx-map-loader/load (tmx-map-loader/create) "maps/modules.tmx")
        scaled-grid (place-step schema-tiled-map
                                scale
                                scaled-grid
                                grid
                                (filter #(= :ground (get-in grid %)) (for [x (range (count grid)) y (range (count (grid 0)))] [x y]))
                                (filter #(= :transition (get-in grid %)) (for [x (range (count grid)) y (range (count (grid 0)))] [x y])))
        tiled-map (create-tiled-map* schema-tiled-map scaled-grid)
        start-position (mapv * start scale)
        can-spawn? #(= "all" (movement-property tiled-map %))
        _ (assert (can-spawn? start-position))
        spawn-positions (flood-fill scaled-grid start-position can-spawn?)
        creatures (for [position spawn-positions
                        :when (and (not= position start-position)
                                   (<= (rand) spawn-rate)
                                   (seq creature-properties))]
                    [position (rand-nth creature-properties)])]
    (add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position start-position}))

(defn- scale-grid [grid start scale]
  (let [w (* (count grid) scale)
        h (* (count (grid 0)) scale)
        xyfn (fn [posi]
               (get-in grid (mapv #(int (/ % scale)) posi)))
        grid (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                                 (range h)))
                   (range w))]
    {:start-position (mapv #(* % scale) start)
     :grid grid}))

(defn- get-rand-weighted-item
  "given a sequence of items and their weight, returns a weighted random item.
  for example {:a 5 :b 1} returns b only in about 1 of 6 cases"
  [weights]
  (let [result (rand-int (reduce + (map #(% 1) weights)))]
    (loop [r 0
           items weights]
      (let [[item weight] (first items)
            r (+ r weight)]
        (if (> r result)
          item
          (recur (int r) (rest items)))))))

(defn- position-tile-fn [grid]
  (let [uf-grounds (for [x [1 5]
                         y (range 5 11)
                         :when (not= [x y] [5 5])]
                     [x y])
        uf-walls (for [x [1]
                       y [13,16,19,22,25,28]]
                   [x y])
        transition? (fn [[x y]]
                      (= :ground (get-in grid [x (dec y)])))
        rand-0-3 (fn [] (get-rand-weighted-item {0 60 1 1 2 1 3 1}))
        rand-0-5 (fn [] (get-rand-weighted-item {0 30 1 1 2 1 3 1 4 1 5 1}))
        [ground-x ground-y] (rand-nth uf-grounds)
        {wall-x 0 wall-y 1} (rand-nth uf-walls)
        [transition-x transition-y] [wall-x (inc wall-y)]
        wall-tile (fn []
                    {:sprite-idx [(+ wall-x (rand-0-5)) wall-y]
                     :movement "none"})
        transition-tile (fn []
                          {:sprite-idx [(+ transition-x (rand-0-5))
                                        transition-y]
                           :movement "none"})
        ground-tile (fn []
                      {:sprite-idx [(+ ground-x (rand-0-3))
                                    ground-y]
                       :movement "all"})]
    (fn [position]
      (case (get-in grid position)
        :wall (wall-tile)
        :transition (if (transition? position)
                      (transition-tile)
                      (wall-tile))
        :ground (ground-tile)))))

(defn- create-tiled-map [grid tile-size create-tile position->tile]
  (let [properties {"width" (count grid)
                    "height" (count (grid 0))
                    "tilewidth" tile-size
                    "tileheight" tile-size}
        layers [{:name "ground"
                 :visible? true
                 :properties {"movement-properties" true}
                 :tiles (for [x (range (count grid))
                              y (range (count (grid 0)))
                              :let [position [x y]]]
                          [position (create-tile (position->tile position))])}]
        tiled-map (tiled-map/create)]
    (doseq [[k v] properties]
      (assert (string? k))
      (map-properties/put! (tiled-map/properties tiled-map) k v))
    (doseq [{:keys [name visible? properties tiles]} layers]
      (assert (string? name))
      (assert (boolean? visible?))
      (let [props (tiled-map/properties tiled-map)
            layer (tiled-map-tile-layer (map-properties/get props "width")
                                        (map-properties/get props "height")
                                        (map-properties/get props "tilewidth")
                                        (map-properties/get props "tileheight")
                                        name
                                        visible?)]
        (doseq [[k v] properties]
          (assert (string? k))
          (map-properties/put! (tiled-map-tile-layer/properties layer) k v))
        (doseq [[[x y] tile] tiles
                :when tile]
          (set-cell! layer x y tile))
        (map-layers/add! (tiled-map/layers tiled-map) layer)))
    tiled-map))

(defn create-uf-caves
  [world-fn-ctx]
  (let [{:keys [level/creature-properties
                textures
                tile-size
                texture-path
                spawn-rate
                scaling
                cave-size
                cave-style]}
        (merge {:tile-size 48
                :texture-path "images/uf_terrain.png"
                :spawn-rate 0.02
                :scaling 3
                :cave-size 200
                :cave-style :wide}
               world-fn-ctx)
        {:keys [start grid]} (create-caves (random/create) cave-size cave-size cave-style)
        _ (assert (= #{:wall :ground} (set (apply concat grid))))
        grid (fix-nads grid)
        create-tile (let [texture (get textures texture-path)]
                      (memoize
                       (fn [& {:keys [sprite-idx movement]}]
                         {:pre [#{"all" "air" "none"} movement]}
                         (let [texture-region (texture-region/create texture
                                                                     (* (sprite-idx 0) tile-size)
                                                                     (* (sprite-idx 1) tile-size)
                                                                     tile-size
                                                                     tile-size)
                               tile (static-tiled-map-tile/create texture-region)]
                           (map-properties/put! (tiled-map-tile/properties tile) "movement" movement)
                           tile))))
        _ (assert (= #{:wall :ground} (set (apply concat grid))))
        {:keys [start-position grid]} (scale-grid grid start scaling)
        grid (let [grid (reduce #(assoc-in %1 %2 :transition) grid
                                (filter (fn [position]
                                          (and (= :wall (get-in grid position))
                                               (some #(= :ground (get-in grid %))
                                                     (get-8-neighbours position))))
                                        (for [x (range (count grid)) y (range (count (grid 0)))] [x y])))]
               (assert (or
                        (= #{:wall :ground :transition} (set (apply concat grid)))
                        (= #{:ground :transition} (set (apply concat grid))))
                       (str "(set (apply concat grid)): " (set (apply concat grid))))
               grid)
        position->tile (position-tile-fn grid)
        tiled-map (create-tiled-map grid tile-size create-tile position->tile)
        can-spawn? #(= "all" (movement-property tiled-map %))
        _ (assert (can-spawn? start-position))
        level (inc (rand-int 6))
        creatures (filter #(= level (:creature/level %)) creature-properties)
        spawn-positions (flood-fill grid start-position can-spawn?)
        creatures (for [position spawn-positions
                        :when (and (not= position start-position)
                                   (<= (rand) spawn-rate))]
                    [position (rand-nth creatures)])]
    (add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position start-position}))

(defn- known-slot? [slot]
  (slots slot))

(defn- cells-and-items [inventory slot]
  (assert (known-slot? slot) (str "Slot :" (pr-str slot)))
  (let [grid (slot inventory)]
    (for [x (range (count grid))
          y (range (count (grid 0)))
          :let [position [x y]]]
      [[slot position] (get-in grid position)])))

(defn- free-cell [inventory slot]
  (assert (known-slot? slot) (str "Slot :" (pr-str slot)))
  (first (filter (fn [[_cell cell-item]]
                   (nil? cell-item))
                 (cells-and-items inventory slot))))

(defn valid-item? [item]
  (let [keyset (set (keys item))]
    (or (= #{:property/id
             :property/pretty-name
             :entity/image
             :item/slot
             :stats/modifiers} keyset)
        (= #{:property/id
             :property/pretty-name
             :entity/image
             :item/slot} keyset))))

(defn can-pickup-item? [inventory item]
  (assert (valid-item? item))
  (or (free-cell inventory (:item/slot item))
      (free-cell inventory :inventory.slot/bag)))

(defn item-at [inventory [slot position]]
  (get-in (slot inventory) position))

(defn valid-slot? [[slot _] item]
  (or (= :inventory.slot/bag slot)
      (= (:item/slot item) slot)))

(defn applies-modifiers? [[slot _]]
  (not= :inventory.slot/bag slot))

(defn enemy [faction]
  (case faction
    :evil :good
    :good :evil))

(defn timer-stopped? [{:keys [stop-time]} elapsed-time]
  (>= elapsed-time stop-time))

(defn timer-ratio [{:keys [duration stop-time] :as timer} elapsed-time]
  {:post [(<= 0 % 1)]}
  (if (timer-stopped? timer elapsed-time)
    0
    ; min 1 because floating point math inaccuracies
    (min 1 (/ (- stop-time elapsed-time) duration))))

(defn- remove-newlines [s]
  (let [new-s (-> s
                  (str/replace "\n\n" "\n")
                  (str/replace #"^\n" "")
                  str/trim-newline)]
    (if (= (count new-s) (count s))
      s
      (remove-newlines new-s))))

(defn- inv-get-cell [inventory-window cell]
  (->> (group/children (group/find-actor inventory-window "inventory-cell-table"))
       (filter #(= (actor/user-object %) cell))
       first))

(defn set-item!* [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (inv-get-cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (actor/user-object image-widget))]
    (image/set-drawable! image-widget (doto (texture-region-drawable/create texture-region)
                                        (texture-region-drawable/set-min-size! cell-size cell-size)))
    (actor/add-listener! cell-widget (text-tooltip/create tooltip-text skin))
    nil))

(defn item-info-text [item]
  (assert (valid-item? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (mods-format-text (:stats/modifiers item)) "[]"))])))

(defn add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        horizontal-group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")
        button-group (actor/user-object horizontal-group)
        button (doto (image-button/create
                      (doto (texture-region-drawable/create texture-region)
                        (texture-region-drawable/set-min-size! (* scale (texture-region/region-width texture-region))
                                                               (* scale (texture-region/region-height texture-region)))))
                 (actor/add-listener! (text-tooltip/create tooltip-text skin))
                 (actor/set-user-object! skill-id))]
    (group/add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))

#_(defn remove-skill!
  [action-bar skill-id]
  (let [horizontal-group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")
        button-group (actor/user-object horizontal-group)
        button (get horizontal-group skill-id)]
    (actor/remove! button)
    (button-group/remove! button-group button)
    nil))

(defn remove-item!* [inventory-window cell]
  (let [cell-widget (inv-get-cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")]
    (image/set-drawable! image-widget (:background-drawable (actor/user-object image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn remove-item! [stage cell]
  (-> (stage/root stage)
      (group/find-actor "moon.ui.windows.inventory")
      (remove-item!* cell)))

(defn toggle-inventory-visible! [stage]
  (-> (stage/root stage)
      (group/find-actor "moon.ui.windows.inventory")
      toggle-visible!))

(defn close-windows! [stage]
  (->> (group/children (group/find-actor (stage/root stage) "moon.ui.windows"))
       (run! #(actor/set-visible! % false))))

(defn toggle-entity-info! [stage]
  (-> (stage/root stage)
      (group/find-actor "moon.ui.windows.entity-info")
      toggle-visible!))

(defn show-modal! [skin stage {:keys [title text button-text on-click]}]
  (assert (not (group/find-actor (stage/root stage) "moon.ui.modal-window")))
  (let [^String title title
        ^String text text
        window (window/create title skin)]
    (table/add! window (label/create text skin))
    (table/row! window)
    (table/add! window (doto (text-button/create button-text skin)
                   (actor/add-listener! (change-listener/create
                                    (fn [_event _actor]
                                      (actor/remove! (group/find-actor (stage/root stage) "moon.ui.modal-window"))
                                      (on-click))))))
    (table/row! window)
    (layout/pack! window)
    (window/set-modal! window true)
    (actor/set-name! window "moon.ui.modal-window")
    (actor/set-position! window
                  (/ (viewport/world-width (stage/viewport stage)) 2)
                  (* (viewport/world-height (stage/viewport stage)) (/ 3 4))
                  align/center)
    (stage/add-actor! stage window)))

(defn- create-info-window
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [skin skin
        label (label/create "MY LABEL TEXT" skin)
        window (window/create title skin)
        c (table/add! window label)]
    (cell/expand! c)
    (table/row! window)
    (layout/pack! window)
    (actor/set-name! window actor-name)
    (actor/set-visible! window visible?)
    (let [[x y] position]
      (actor/set-position! window x y))
    (group/add-actor! window (actor/create
                               {:act!
                                (fn [this _delta]
                                  (when (actor/stage this)
                                    (label/set-text! label (set-label-text!)))
                                  (layout/pack! window))
                                :draw!
                                (fn [_this _batch _parent-alpha])}))
    window))

(defn nearest-entity [cell faction]
  (-> cell faction :eid))

(defn nearest-entity-distance [cell faction]
  (-> cell faction :distance))

(defn cached-adjacent-cells [grid cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (into [] (keep #(get-in grid %)) (get-8-neighbours (:position @cell)))]
      (swap! cell assoc :adjacent-cells result)
      result)))

(defn nearest-enemy [grid entity]
  (nearest-entity @(get-in grid (mapv int (:entity/position entity)))
                  (enemy (:entity/faction entity))))

(defn nearest-enemy-distance [grid entity]
  (nearest-entity-distance @(get-in grid (mapv int (:entity/position entity)))
                           (enemy (:entity/faction entity))))

(defn add-text-effect [entity text duration timer-create]
  (assoc entity :entity/string-effect
         (if-let [existing (:entity/string-effect entity)]
           (-> existing
               (update :text str "\n" text)
               (update :counter timer-increment duration))
           {:text text
            :counter (timer-create duration)})))

(defn- inventory-cell-path [[slot [x y]]]
  [:entity/inventory slot x y])

(defn set-item [entity cell item]
  (assert (and (nil? (get-in entity (inventory-cell-path cell)))
               (valid-slot? cell item)))
  (cond-> (assoc-in entity (inventory-cell-path cell) item)
    (applies-modifiers? cell)
    (update :entity/stats add-mods (:stats/modifiers item))))

(defn remove-item [entity cell]
  (let [item (get-in entity (inventory-cell-path cell))]
    (assert item)
    (cond-> (assoc-in entity (inventory-cell-path cell) nil)
      (applies-modifiers? cell)
      (update :entity/stats remove-mods (:stats/modifiers item)))))

(defrecord GridCell 
  [position
   middle
   adjacent-cells
   movement
   entities
   occupied
   good
   evil])

(defn blocks-vision? [{:keys [movement]}]
  (= movement :none))

(defn cell-blocked? [{:keys [movement]} z-order]
  (case movement
    :none true
    :air (case z-order
           :z-order/flying false
           :z-order/ground true)
    :all false))

(defn pf-blocked? [cell]
  (cell-blocked? cell :z-order/ground))

(defn occupied-by-other? [{:keys [occupied]} eid]
  (some #(not= % eid) occupied))

(defn direction [entity other-entity]
  (v2-direction (:entity/position entity)
                (:entity/position other-entity)))

(defn start-point [entity target-entity]
  (mapv + (:entity/position entity)
        (v2-scale (direction entity target-entity)
                  (/ (:entity/width entity) 2))))

(defn end-point [entity target-entity maxrange]
  (mapv + (start-point entity target-entity)
        (v2-scale (direction entity target-entity)
                  maxrange)))

(defn in-range? [entity target-entity maxrange]
  (< (- (float (v2-distance (:entity/position entity)
                            (:entity/position target-entity)))
        (float (/ (:entity/width entity)  2))
        (float (/ (:entity/width target-entity) 2)))
     (float maxrange)))

(defn rectangle
  [{:keys [entity/position
           entity/width
           entity/height]}]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (rectangle/create x y width height)))

(defn overlaps? [entity other-entity]
  (rectangle/overlaps? (rectangle entity)
                       (rectangle other-entity)))

(defn touched-tiles
  [{:keys [entity/position
           entity/width
           entity/height]}]
  (touched-tiles*
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))

(let [order (get-8-neighbours [0 0])
      diagonal? (fn [[^int x ^int y]]
                  (and (not (zero? x))
                       (not (zero? y))))]
  (def ^:private diagonal-check-indizes
    (into {} (for [[x y] (filter diagonal? order)]
               [(.indexOf ^clojure.lang.PersistentVector order [x y])
                (vec (keep-indexed (fn [i p] (when (#{[x 0] [0 y]} p) i)) order))]))))

(defn- not-allowed-diagonal? [at-idx adjacent-cells]
  (when-let [[a b] (get diagonal-check-indizes at-idx)]
    (and (nil? (adjacent-cells a))
         (nil? (adjacent-cells b)))))

(defn- remove-not-allowed-diagonals [adjacent-cells]
  (remove nil?
          (map-indexed
           (fn [idx cell]
             (when-not (or (nil? cell)
                           (not-allowed-diagonal? idx adjacent-cells))
               cell))
           adjacent-cells)))

(defn- filter-viable-cells [eid adjacent-cells]
  (remove-not-allowed-diagonals
   (mapv #(when-not (or (pf-blocked? @%)
                        (occupied-by-other? @% eid))
            %)
         adjacent-cells)))

(defn- get-min-dist-cell [distance-to cells]
  (let [cells (filter distance-to cells)]
    (when (seq cells)
      (apply min-key distance-to cells))))

(defn- viable-cell? [grid distance-to own-dist eid cell]
  (when-let [best-cell (get-min-dist-cell
                        distance-to
                        (filter-viable-cells eid (cached-adjacent-cells grid cell)))]
    (when (< (float (distance-to best-cell)) (float own-dist))
      cell)))

(defn- find-next-cell
  "returns {:target-entity eid} or {:target-cell cell}. Cell can be nil."
  [grid eid own-cell]
  (let [faction (enemy (:entity/faction @eid))
        distance-to #(nearest-entity-distance @% faction)
        nearest-entity-at #(nearest-entity @% faction)
        own-dist (distance-to own-cell)
        adjacent-cells (cached-adjacent-cells grid own-cell)]
    (if (and own-dist (zero? (float own-dist)))
      {:target-entity (nearest-entity-at own-cell)}
      (if-let [adjacent-cell (first (filter #(and (distance-to %)
                                                  (zero? (float (distance-to %))))
                                            adjacent-cells))]
        {:target-entity (nearest-entity-at adjacent-cell)}
        {:target-cell (let [cells (filter-viable-cells eid adjacent-cells)
                            min-key-cell (get-min-dist-cell distance-to cells)]
                        (cond
                          (not min-key-cell) ; red
                          own-cell

                          (not own-dist)
                          min-key-cell

                          (> (float (distance-to min-key-cell)) (float own-dist)) ; red
                          own-cell

                          (< (float (distance-to min-key-cell)) (float own-dist)) ; green
                          min-key-cell

                          (= (distance-to min-key-cell) own-dist) ; yellow
                          (or
                           (some #(viable-cell? grid distance-to own-dist eid %) cells)
                           own-cell)))}))))

(defn- inside-cell? [grid entity cell]
  (let [cells (keep #(get-in grid %) (touched-tiles entity))]
    (and (= 1 (count cells))
         (= cell (first cells)))))

(defn find-direction [grid eid]
  (let [position (:entity/position @eid)
        own-cell (get-in grid (mapv int position))
        {:keys [target-entity target-cell]} (find-next-cell grid eid own-cell)]
    (cond
      target-entity
      (v2-direction position (:entity/position @target-entity))

      (nil? target-cell)
      nil

      :else
      (when-not (and (= target-cell own-cell)
                     (occupied-by-other? @own-cell eid))
        (when-not (inside-cell? grid @eid target-cell)
          (v2-direction position (:middle @target-cell)))))))

(defn- potential-field-step [grid faction last-marked-cells]
  (let [marked-cells (transient [])
        distance #(nearest-entity-distance % faction)
        nearest-entity-at #(nearest-entity % faction)
        marked? faction]
    (doseq [cell (sort-by #(distance @%) last-marked-cells)
            adjacent-cell (cached-adjacent-cells grid cell)
            :let [cell* @cell
                  adjacent-cell* @adjacent-cell]
            :when (not (or (pf-blocked? adjacent-cell*)
                           (marked? adjacent-cell*)))
            :let [distance-value (+ (float (distance cell*))
                                    (float (if (diagonal? (:position cell*)
                                                          (:position adjacent-cell*))
                                             1.4 ; square root of 2 * 10
                                             1)))]]
      (swap! adjacent-cell assoc faction {:distance distance-value
                                          :eid (nearest-entity-at cell*)})
      (conj! marked-cells adjacent-cell))
    (persistent! marked-cells)))

(defn- generate-potential-field
  [grid faction tiles->entities max-iterations]
  (let [entity-cell-seq (for [[tile eid] tiles->entities]
                          [eid (get-in grid tile)])
        marked (map second entity-cell-seq)]
    (doseq [[eid cell] entity-cell-seq]
      (swap! cell assoc faction {:distance 0
                                 :eid eid}))
    (loop [marked-cells     marked
           new-marked-cells marked
           iterations 0]
      (if (= iterations max-iterations)
        marked-cells
        (let [new-marked (potential-field-step grid faction new-marked-cells)]
          (recur (concat marked-cells new-marked)
                 new-marked
                 (inc iterations)))))))

; Assumption: The map contains no not-allowed diagonal cells, diagonal wall cells where both
; adjacent cells are walls and blocked.
; (important for wavefront-expansion and field-following)
; * entities do not move to NADs (they remove them)
; * the potential field flows into diagonals, so they should be reachable too.
;
; TODO assert somewhere/at map load no NAD's and @ potential field init & remove from
; potential-field-following the removal of NAD's.

; TODO remove max pot field movement player screen + 10 tiles as of screen size
; => is coupled to max-steps & also
; to friendly units follow player distance

(defn update!
  [grid pf-cache faction entities max-iterations]
  (let [tiles->entities (let [entities (filter #(= (:entity/faction @%) faction)
                                               entities)]
                          (zipmap (map #(mapv int (:entity/position @%)) entities)
                                  entities))
        last-state   [faction :tiles->entities]
        marked-cells [faction :marked-cells]]
    (when-not (= (get-in @pf-cache last-state) tiles->entities)
      (swap! pf-cache assoc-in last-state tiles->entities)
      (doseq [cell (get-in @pf-cache marked-cells)]
        (swap! cell dissoc faction))
      (swap! pf-cache assoc-in marked-cells (generate-potential-field
                                             grid
                                             faction
                                             tiles->entities
                                             max-iterations)))))

(defn valid-position? [grid {:keys [entity/z-order] :as body} entity-id]
  (assert (:entity/collides? body))
  (let [cells* (map deref (keep #(get-in grid %) (touched-tiles body)))]
    (and (not-any? #(cell-blocked? % z-order) cells*)
         (->> (into #{} (mapcat :entities) cells*)
              (not-any? (fn [other-entity]
                          (let [other-entity @other-entity]
                            (and (not= (:entity/id other-entity) entity-id)
                                 (:entity/collides? other-entity)
                                 (overlaps? other-entity
                                                 body)))))))))

(defn try-move [grid body entity-id movement]
  (let [{:keys [direction speed delta-time]} movement
        new-body (update body :entity/position
                         #(mapv (fn [p d] (+ p (* d speed delta-time))) % direction))]
    (when (valid-position? grid new-body entity-id)
      new-body)))

(defn try-move-solid-body [grid body entity-id {[vx vy] :direction :as movement}]
  (let [xdir (math/signum (float vx))
        ydir (math/signum (float vy))]
    (or (try-move grid body entity-id movement)
        (try-move grid body entity-id (assoc movement :direction [xdir 0]))
        (try-move grid body entity-id (assoc movement :direction [0 ydir])))))

(defn body->occupied-cells
  [grid {:keys [entity/position
                entity/width
                entity/height]
         :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (keep #(get-in grid %) (touched-tiles body))
    [(get-in grid (mapv int position))]))

(defn set-occupied-cells! [grid eid]
  (let [cells (body->occupied-cells grid @eid)]
    (doseq [cell cells]
      (assert (not (get (:occupied @cell) eid)))
      (swap! cell update :occupied conj eid))
    (swap! eid assoc :entity/occupied-cells cells)))

(defn set-touched-cells! [grid eid]
  (let [cells (keep #(get-in grid %) (touched-tiles @eid))]
    (assert (not-any? nil? cells))
    (swap! eid assoc :entity/touched-cells cells)
    (doseq [cell cells]
      (assert (not (get (:entities @cell) eid)))
      (swap! cell update :entities conj eid))))

(defn remove-from-occupied-cells! [_ eid]
  (doseq [cell (:entity/occupied-cells @eid)]
    (assert (get (:occupied @cell) eid))
    (swap! cell update :occupied disj eid)))

(defn remove-from-touched-cells! [_ eid]
  (doseq [cell (:entity/touched-cells @eid)]
    (assert (get (:entities @cell) eid))
    (swap! cell update :entities disj eid)))

(defn point->entities [grid pos]
  (when-let [cell (get-in grid (mapv int pos))]
    (filter #(rectangle/contains? (rectangle @%) (first pos) (second pos))
            (:entities @cell))))

(defn create-animation
  [{:keys [animation/frames
           animation/frame-duration
           animation/looping?
           delete-after-stopped?]}]
  (assert (not (and looping? delete-after-stopped?)))
  {:frames (vec frames)
   :frame-duration frame-duration
   :looping? looping?
   :cnt 0
   :maxcnt (* (count frames) (float frame-duration))
   :delete-after-stopped? delete-after-stopped?})

(defn- create-grid [tiled-map]
  (let [props (tiled-map/properties tiled-map)
        w (map-properties/get props "width")
        h (map-properties/get props "height")
        xyfn (fn [position]
               (atom
                (map->GridCell
                 {:position position
                  :middle (mapv (partial + 0.5) position)
                  :movement (case (movement-property tiled-map position)
                               "none" :none
                               "air" :air
                               "all" :all)
                  :entities #{}
                  :occupied #{}})))]
    (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                        (range h)))
          (range w))))

(defn- create-raycaster [grid]
  (let [width (count grid)
        height (count (grid 0))
        arr (make-array Boolean/TYPE width height)]
    (doseq [cell (map deref (apply concat grid))]
      (let [[x y] (:position cell)]
        (aset arr x y (boolean (blocks-vision? cell)))))
    [arr width height]))

(defn- button?
  [actor]
  (or (button/button? actor)
      (when-let [parent (actor/parent actor)]
        (button/button? parent))))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (actor/parent actor)
                            (= "inventory-cell" (actor/name (actor/parent actor)))
                            (actor/user-object (actor/parent actor)))]
    (cond
      inventory-slot
      [:mouseover-actor/inventory-cell inventory-slot]

      (when (label/label? actor)
        (when-let [p (actor/parent actor)]
          (when-let [p (actor/parent p)]
            (and (window/window? p)
                 (= (window/title-label p) actor)))))
      [:mouseover-actor/window-title-bar]

      (button? actor)
      [:mouseover-actor/button]

      :else
      [:mouseover-actor/unspecified])))

(defn- tile-color-setter*
  [{:keys [ray-blocked?
           explored-tile-corners
           light-position
           see-all-tiles?
           explored-tile-color
           visible-tile-color
           invisible-tile-color]}]
  #_(reset! do-once false)
  (let [light-cache (atom {})]
    (fn tile-color-setter [_color x y]
      (let [position [(int x) (int y)]
            explored? (get-in @explored-tile-corners position) ; TODO needs int call ?
            base-color (if explored?
                         explored-tile-color
                         invisible-tile-color)
            cache-entry (get @light-cache position :not-found)
            blocked? (if (= cache-entry :not-found)
                       (let [blocked? (ray-blocked? light-position position)]
                         (swap! light-cache assoc position blocked?)
                         blocked?)
                       cache-entry)]
        #_(when @do-once
            (swap! ray-positions conj position))
        (if blocked?
          (if see-all-tiles?
            visible-tile-color
            base-color)
          (do (when-not explored?
                (swap! explored-tile-corners assoc-in (mapv int position) true))
              visible-tile-color))))))

(defn- white-pixel-texture []
  (let [pixmap (doto (pixmap/create 1 1 pixmap-format/RGBA8888)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (texture/from-pixmap pixmap)]
    (disposable/dispose! pixmap)
    texture))

(defn- draw-text!
  [batch
   default-font
   unit-scale
   {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        scale (or scale 1)
        font-data (bitmap-font/data font)
        old-scale (bitmap-font-data/scale-x font-data)
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (bitmap-font-data/set-scale! font-data (* old-scale scale))
    (bitmap-font/draw font
                      batch
                      text
                      (float x)
                      (float (+ y (if up?
                                    (-> text
                                        (str/split #"\n")
                                        count
                                        (* (bitmap-font/line-height font)))
                                    0)))
                      (float target-width)
                      align/center
                      wrap?)
    (bitmap-font-data/set-scale! font-data old-scale)))

(defn- draw-texture-region! [batch unit-scale texture-region [x y] & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(texture-region/region-width texture-region)
                                (texture-region/region-height texture-region)]]
                (if (= unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * unit-scale))
                        dimensions)))]
    (if center?
      (batch/draw batch
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
      (batch/draw batch
                  texture-region
                  x
                  y
                  w
                  h))))

(defn- create-fsm
  [fsm initial-state]
  (assoc (fsm initial-state nil) :state initial-state))

(defn- empty-inventory []
  (->> #:inventory.slot{:bag      [6 4]
                        :weapon   [1 1]
                        :shield   [1 1]
                        :helm     [1 1]
                        :chest    [1 1]
                        :leg      [1 1]
                        :glove    [1 1]
                        :boot     [1 1]
                        :cloak    [1 1]
                        :necklace [1 1]
                        :rings    [2 1]}
       (map (fn [[slot [width height]]]
              [slot (mapv (fn [x] (mapv (fn [y] nil)
                                        (range height)))
                          (range width))]))
       (into {})))

(defn- skills->id-map [skills]
  (reduce (fn [m {:keys [property/id] :as skill}]
            (assert (not (contains? m id)))
            (assoc m id skill))
          {}
          skills))

(defn- place-items [entity items]
  (reduce (fn [entity item]
            (assert (valid-item? item))
            (let [[cell cell-item] (can-pickup-item? (:entity/inventory entity) item)]
              (assert cell)
              (assert (nil? cell-item))
              (set-item entity cell item)))
          entity
          items))

(defn- spawn-creature [{:keys [position creature-property components]}]
  (assert creature-property)
  (let [entity (-> creature-property
                   (assoc :entity/position position
                          :entity/collides? true
                          :entity/z-order (if (:entity/flying? creature-property)
                                            :z-order/flying
                                            :z-order/ground))
                   (assoc :entity/destroy-audiovisual :audiovisuals/creature-die))
        {:keys [fsm initial-state]} (:entity/fsm components)
        components (assoc components
                          :entity/fsm (create-fsm fsm initial-state)
                          initial-state nil)]
    (assert (not-any? #(contains? entity %) (keys components)))
    (let [items (:entity/inventory entity)
          entity (cond-> (merge entity components)
                   (:entity/skills entity) (update :entity/skills skills->id-map))]
      (if items
        (-> entity
            (assoc :entity/inventory (empty-inventory))
            (place-items items))
        entity))))

(defn- spawn-effect [position components]
  (assoc components
         :entity/width 0.5
         :entity/height 0.5
         :entity/z-order :z-order/effect
         :entity/position position))

(defn timer-create [duration elapsed-time]
  {:pre [(>= duration 0)]}
  {:duration duration
   :stop-time (+ elapsed-time duration)})

(defn- spawn-item [position item]
  {:entity/position position
   :entity/width 0.75
   :entity/height 0.75
   :entity/z-order :z-order/on-ground
   :entity/image (:entity/image item)
   :entity/item item
   :entity/clickable {:type :clickable/item
                      :text (:property/pretty-name item)}})

(defn- spawn-line [{:keys [start end duration color thick?]}]
  (spawn-effect start
                {:entity/line-render {:thick? thick? :end end :color color}
                 :entity/delete-after-duration duration}))

(defn- spawn-projectile
  [{:keys [position direction faction]}
   {:keys [entity/image
           projectile/max-range
           projectile/speed
           entity-effects
           projectile/size
           projectile/piercing?]}]
  {:entity/position position
   :entity/width size
   :entity/height size
   :entity/z-order :z-order/flying
   :entity/rotation-angle (let [angle (math/to-degrees
                                       (math/atan2 (v2-crs [0 1] direction)
                                                   (v2-dot [0 1] direction)))]
                            (if (neg? angle)
                              (+ angle 360)
                              angle))
   :entity/image image
   :entity/faction faction
   :entity/delete-after-duration (/ max-range speed)
   :entity/destroy-audiovisual :audiovisuals/hit-wall
   :entity/projectile-collision {:entity-effects entity-effects
                                 :piercing? piercing?
                                 :direction direction
                                 :speed speed}})

(defn create-skin! [file-handle]
  (let [s (skin/create file-handle)]
    (bitmap-font-data/set-markup-enabled!
     (bitmap-font/data (skin/font s "default-font"))
     true)
    s))

(defn create-viewport! [width height world-unit-scale]
  (let [world-width (* width world-unit-scale)
        world-height (* height world-unit-scale)]
    (fit-viewport/create world-width
                         world-height
                         (doto (orthographic-camera/create)
                           (orthographic-camera/set-to-ortho! false world-width world-height)))))

(defn create-default-font! [internal {:keys [path size quality-scaling use-integer-positions?]}]
  (let [generator (free-type-font-generator/create (internal path))
        parameter (doto (font-parameter/create)
                    (font-parameter/set-size! (* size quality-scaling))
                    (font-parameter/set-min-filter! texture-filter/linear)
                    (font-parameter/set-mag-filter! texture-filter/linear))
        font (free-type-font-generator/generate-font generator parameter)
        font-data (bitmap-font/data font)]
    (disposable/dispose! generator)
    (bitmap-font-data/set-scale! font-data (/ quality-scaling))
    (bitmap-font-data/set-markup-enabled! font-data true)
    (bitmap-font/set-use-integer-positions! font use-integer-positions?)
    font))

(defn create-vampire-level [_]
  {:tiled-map (tmx-map-loader/load (tmx-map-loader/create) "maps/vampire.tmx")
   :start-position [32 71]})

(defn spawn-positions [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (map-layers/get (tiled-map/layers tiled-map) layer-name)]
    (for [x (range (tiled-map-tile-layer/width layer))
          y (range (tiled-map-tile-layer/height layer))
          :let [cell (tiled-map-tile-layer/get-cell layer x y)]
          :when cell
          :let [value (map-properties/get (tiled-map-tile/properties (tiled-map-tile-layer-cell/get-tile cell)) property-key)]
          :when value]
      [[x y] value])))

(defn spawn-map-creatures [build-property tiled-map start-position fsms entity*]
  (mapcat (fn [[position creature-id]]
            (when (not= position start-position)
              [[:tx/spawn-entity
                (entity* (spawn-creature {:position (mapv (partial + 0.5) position)
                                          :creature-property (build-property (keyword creature-id))
                                          :components {:entity/fsm {:fsm (:npc fsms)
                                                                    :initial-state :npc-sleeping}
                                                       :entity/faction :evil}}))]]))
          (spawn-positions tiled-map)))

(defn player-ui-txs [info-text entity]
  (concat
    (map (fn [skill]
           [:tx/set-skill (:property/id skill) (:entity/image skill) (info-text skill)])
         (vals (:entity/skills entity)))
    (for [[slot grid] (:entity/inventory entity)
          x (range (count grid))
          y (range (count (grid 0)))
          :let [position [x y]
                item (get-in grid position)]
          :when item]
      [:tx/set-item [slot position] item])))

(defn handle-clicked-inventory-cell
  [player-eid cell]
  (let [entity @player-eid
        inventory (:entity/inventory entity)
        item-in-cell (item-at inventory cell)
        item-on-cursor (:entity/item-on-cursor entity)]
    (cond
      (and (not item-on-cursor) 
           item-in-cell)
      [[:tx/swap player-eid remove-item cell]
       [:tx/swap player-eid assoc :entity/item-on-cursor item-in-cell]
       [:tx/sound "bfxr_takeit.wav"]
       [:tx/remove-item cell]]

      (and (not item-on-cursor) (not item-in-cell))
      ; FIXME handle
      nil  

      ; TODO 2x more cases for not-valid-slot (cant place it here -> handle all cases -> clarity)
      (and item-on-cursor
           (not item-in-cell)
           (valid-slot? cell item-on-cursor))
      [[:tx/swap player-eid set-item cell item-on-cursor]
       [:tx/swap player-eid dissoc :entity/item-on-cursor]
       [:tx/sound "bfxr_itemput.wav"]
       [:tx/set-item cell item-on-cursor]]

      (and item-on-cursor
           item-in-cell
           (valid-slot? cell item-on-cursor))
      [[:tx/swap player-eid remove-item cell]
       [:tx/swap player-eid set-item cell item-on-cursor]
       [:tx/swap player-eid assoc :entity/item-on-cursor item-in-cell]
       [:tx/sound "bfxr_itemput.wav"]
       [:tx/remove-item cell]
       [:tx/set-item cell item-on-cursor]])))

(defn draw-cell-debug
  [factions-iterations shape-drawer viewport show-cell-entities? show-cell-occupied? show-potential-field-colors? grid]
  (let [camera (viewport/camera viewport)
        plane-points (mapv (fn [v3]
                             [(vector3/x v3) (vector3/y v3) (vector3/z v3)])
                           (frustum/plane-points (orthographic-camera/frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    (doseq [x (range (int left-x) (int right-x))
            y (range (int bottom-y) (+ 2 (int top-y)))
            :let [cell-atom (get-in grid [x y])]
            :when cell-atom
            :let [cell* @cell-atom]]
      (when (and show-cell-entities? (seq (:entities cell*)))
        (filled-rectangle! shape-drawer x y 1 1 (color/float-bits 1 0 0 0.6)))
      (when (and show-cell-occupied? (seq (:occupied cell*)))
        (filled-rectangle! shape-drawer x y 1 1 (color/float-bits 0 0 1 0.6)))
      (when-let [faction show-potential-field-colors?]
        (let [{:keys [distance]} (faction cell*)]
          (when distance
            (let [ratio (/ distance (factions-iterations faction))]
              (filled-rectangle! shape-drawer x y 1 1 (color/float-bits ratio (- 1 ratio) ratio 0.6)))))))))

(defn- draw-fn-grid [shape-drawer leftx bottomy gridw gridh cellw cellh color-float-bits]
  (let [w (* (float gridw) (float cellw))
        h (* (float gridh) (float cellh))
        topy (+ (float bottomy) (float h))
        rightx (+ (float leftx) (float w))]
    (doseq [idx (range (inc (float gridw)))
            :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
      (line! shape-drawer [linex topy] [linex bottomy] color-float-bits))
    (doseq [idx (range (inc (float gridh)))
            :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
      (line! shape-drawer [leftx liney] [rightx liney] color-float-bits))))

(defn draw-tile-grid
  [shape-drawer viewport]
  (let [camera (viewport/camera viewport)
        plane-points (mapv (fn [v3]
                             [(vector3/x v3) (vector3/y v3) (vector3/z v3)])
                           (frustum/plane-points (orthographic-camera/frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))]
    (draw-fn-grid shape-drawer
                  (int left-x)
                  (int bottom-y)
                  (inc (int (viewport/world-width viewport)))
                  (+ 2 (int (viewport/world-height viewport)))
                  1
                  1
                  (color/float-bits 1 1 1 0.8))))

(defn highlight-mouseover-tile
  [shape-drawer world-mouse-position grid]
  (let [[x y] (mapv int world-mouse-position)
        cell (when-let [cell-atom (get-in grid [x y])]
               @cell-atom)]
    (when (and cell (#{:air :none} (:movement cell)))
      (rectangle! shape-drawer x y 1 1
                         (case (:movement cell)
                           :air (color/float-bits 1 1 0 0.5)
                           :none (color/float-bits 1 0 0 0.5))))))

(defn clickable-draw
  [v entity batch default-font unit-scale]
  (let [{:keys [text]} v
        {:keys [entity/position entity/height entity/mouseover?]} entity]
    (when (and mouseover? text)
      (let [[x y] position]
        (draw-text! batch
                    default-font
                    unit-scale
                    {:text text
                     :x x
                     :y (+ y (/ height 2))
                     :up? true})))))

(defn current-frame [{:keys [frames cnt frame-duration]}]
  (frames (min (int (/ (float cnt) (float frame-duration)))
               (dec (count frames)))))

(defn animation-tick*
  [{:keys [looping? cnt maxcnt] :as animation} delta-time]
  (let [maxcnt (float maxcnt)
        newcnt (+ (float cnt) (float delta-time))]
    (assoc animation :cnt (cond (< newcnt maxcnt) newcnt
                                looping? (min maxcnt (- newcnt maxcnt))
                                :else maxcnt))))

(defn animation-tick
  [entity delta-time]
  (let [{:keys [delete-after-stopped? looping? cnt maxcnt]} (:entity/animation entity)]
    (cond-> (update entity :entity/animation animation-tick* delta-time)
      (and delete-after-stopped?
           (not looping?)
           (>= cnt maxcnt))
      (assoc :entity/destroyed? true))))

(defn animation-draw
  [animation entity batch unit-scale image->texture-region]
  (draw-texture-region! batch
                        unit-scale
                        (image->texture-region (current-frame animation))
                        (:entity/position entity)
                        {:center? true
                         :rotation (or (:entity/rotation-angle entity) 0)}))

(defn image-draw
  [image entity batch unit-scale image->texture-region]
  (draw-texture-region! batch unit-scale
                        (image->texture-region image)
                        (:entity/position entity)
                        {:center? true
                         :rotation (or (:entity/rotation-angle entity) 0)}))

(defn line-render-draw
  [v entity shape-drawer]
  (let [{:keys [thick? end color]} v
        position (:entity/position entity)]
    (if thick?
      (with-line-width shape-drawer 4
        (line! shape-drawer position end color))
      (line! shape-drawer position end color))))

(defn mouseover?-draw
  [entity shape-drawer player]
  (let [{:keys [entity/position entity/width entity/height entity/faction]} entity
        color (cond (= faction (enemy (:entity/faction player)))
                    (color/float-bits 1 0 0 0.4)
                    (= faction (:entity/faction player))
                    (color/float-bits 0 1 0 0.4)
                    :else
                    (color/float-bits 1 1 1 0.4))]
    (with-line-width shape-drawer 5
      (ellipse! shape-drawer position
                (/ width 2)
                (/ height 2)
                color))))

(defn stats-draw
  [entity shape-drawer unit-scale]
  (let [ratio (val-max-ratio (get-hitpoints (:entity/stats entity)))]
    (when (or (< ratio 1) (:entity/mouseover? entity))
      (let [{:keys [entity/position entity/width entity/height]} entity
            [x y] position
            x (- x (/ width  2))
            y (+ y (/ height 2))
            height (* 5 unit-scale)
            border (* 1 unit-scale)]
        (filled-rectangle! shape-drawer x y width height (color/float-bits 0 0 0 1))
        (filled-rectangle! shape-drawer
                           (+ x border)
                           (+ y border)
                           (- (* width ratio) (* 2 border))
                           (- height (* 2 border))
                           (cond
                             (> ratio 0.75) (color/float-bits 0 0.8 0 1)
                             (> ratio 0.5) (color/float-bits 0 0.5 0 1)
                             (> ratio 0.25) (color/float-bits 0.5 0.5 0 1)
                             :else (color/float-bits 0.5 0 0 1)))))))

(defn string-effect-draw
  [v entity batch default-font unit-scale]
  (let [{:keys [text]} v
        [x y] (:entity/position entity)]
    (draw-text! batch
                default-font
                unit-scale
                {:text text
                 :x x
                 :y (+ y
                       (/ (:entity/height entity) 2)
                       (* 5 unit-scale))
                 :scale 2
                 :up? true})))

(defn temp-modifier-draw
  [entity shape-drawer]
  (filled-circle! shape-drawer (:entity/position entity) 0.5 (color/float-bits 0.5 0.5 0.5 0.4)))

(defn npc-sleeping-draw
  [entity batch default-font unit-scale]
  (let [{:keys [entity/position entity/height]} entity
        [x y] position]
    (draw-text! batch
                default-font
                unit-scale
                {:text "zzz"
                 :x x
                 :y (+ y (/ height 2))
                 :up? true})))

(defn stunned-draw
  [entity shape-drawer]
  (circle! shape-drawer (:entity/position entity) 0.5 (color/float-bits 1 1 1 0.6)))

(defn- draw-entity-rectangle!
  [shape-drawer entity color-float-bits]
  (let [{:keys [entity/position entity/width entity/height]} entity
        [x y] [(- (position 0) (/ width 2))
               (- (position 1) (/ height 2))]]
    (rectangle! shape-drawer x y width height color-float-bits)))

(defn draw-entities!
  [draw-component! shape-drawer raycaster active-entities player-entity show-body-bounds? render-z-order render-layers]
  (doseq [[z-order entities] (sort-by (comp render-z-order key) <
                                      (group-by :entity/z-order (map deref active-entities)))
          render-layer render-layers
          entity entities
          :when (or (= z-order :z-order/effect)
                    (line-of-sight? raycaster player-entity entity))]
    (when show-body-bounds?
      (draw-entity-rectangle! shape-drawer
                              entity
                              (if (:entity/collides? entity)
                                (color/float-bits 1 1 1 1)
                                (color/float-bits 0.5 0.5 0.5 1))))
    (doseq [[k v] entity
            :when (get render-layer k)]
      (draw-component! k v entity))))

(defn outer-rectangle
  [{[x y] :position :keys [radius]}]
  (let [radius (float radius)
        size (* radius 2)]
    {:x (- (float x) radius)
     :y (- (float y) radius)
     :width  size
     :height size}))

(defn get-friendlies-for-alert [eid faction grid]
  (let [alert-circle {:position (:entity/position @eid)
                      :radius 4}
        [x y] (:position alert-circle)
        gdx-circle (circle/create x y (:radius alert-circle))]
    (->> alert-circle
         outer-rectangle
         touched-tiles*
         (keep #(get-in grid %))
         (map deref)
         (into #{} (mapcat :entities))
         (filter #(intersector/overlaps? gdx-circle (rectangle @%)))
         (filter #(= (:entity/faction @%) faction)))))

(defn alert-friendlies-after-duration-tick
  [v eid _delta-time elapsed-time grid tx-event-txs]
  (let [{:keys [counter faction]} v]
    (when (timer-stopped? counter elapsed-time)
      (into [[:tx/swap eid assoc :entity/destroyed? true]]
            (apply concat
                   (for [friendly-eid (get-friendlies-for-alert eid faction grid)
                         ; TODO called on projectiles
                         :when (:entity/fsm @friendly-eid)]
                     (tx-event-txs friendly-eid :alert)))))))

(defn string-effect-tick
  [v eid _delta-time elapsed-time]
  (let [{:keys [counter]} v]
    (when (timer-stopped? counter elapsed-time)
      [[:tx/swap eid dissoc :entity/string-effect]])))

(defn skills-tick
  [v eid _delta-time elapsed-time]
  (for [{:keys [skill/cooling-down?] :as skill} (vals v)
        :when (and cooling-down?
                   (timer-stopped? cooling-down? elapsed-time))]
    [:tx/swap eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] false]))

(defn temp-modifier-tick
  [v eid _delta-time elapsed-time]
  (let [{:keys [modifiers counter]} v]
    (when (timer-stopped? counter elapsed-time)
      [[:tx/swap eid dissoc :entity/temp-modifier]
       [:tx/swap eid update :entity/stats remove-mods modifiers]])))

(defn projectile-collision-tick
  [v eid delta-time grid applicable-effect-txs]
  (let [{:keys [entity-effects already-hit-bodies piercing? direction speed]} v
        entity (update @eid :entity/position
                       #(mapv (fn [p d] (+ p (* d speed delta-time))) % direction))
        touched-cells (map deref (keep #(get-in grid %) (touched-tiles entity)))
        hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                        (not= (:entity/faction entity)
                                              (:entity/faction @%))
                                        (:entity/collides? @%)
                                        (overlaps? entity
                                                   @%))
                                  (into #{} (mapcat :entities) touched-cells)))
        destroy? (or (and hit-entity (not piercing?))
                     (some #(cell-blocked? % (:entity/z-order entity)) touched-cells))]
    (concat [[:tx/swap eid assoc :entity/position (:entity/position entity)]
             [:tx/update-position eid]]
            (when hit-entity
              [[:tx/swap eid assoc-in [:entity/projectile-collision :already-hit-bodies]
                (conj already-hit-bodies hit-entity)]])
            (when destroy?
              [[:tx/swap eid assoc :entity/destroyed? true]])
            (when hit-entity
              (applicable-effect-txs {:effect/source eid
                                      :effect/target hit-entity}
                                     entity-effects)))))

(defn projectile-effect [v effect-ctx create-projectile]
  (let [source (:effect/source effect-ctx)
        source* @source
        direction (:effect/target-direction effect-ctx)
        size (:projectile/size v)]
    [[:tx/spawn-entity (create-projectile
                         {:position (mapv + (:entity/position source*)
                                          (v2-scale direction
                                                    (+ (/ (:entity/width source*) 2) size 0.1)))
                          :direction direction
                          :faction (:entity/faction source*)}
                         v)]]))

(defn- double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]}
  (let [path-w (+ path-w 0.02)
        v (v2-direction [start-x start-y]
                        [target-y target-y])
        [normal1 normal2] (v2-normal-vectors v)
        normal1 (v2-scale normal1 (/ path-w 2))
        normal2 (v2-scale normal2 (/ path-w 2))
        start1  (mapv + [start-x  start-y]  normal1)
        start2  (mapv + [start-x  start-y]  normal2)
        target1 (mapv + [target-x target-y] normal1)
        target2 (mapv + [target-x target-y] normal2)]
    [start1 target1 start2 target2]))

(defn- affected-targets
  [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(def ^:private spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def ^:private spiderweb-duration 5)

(defn animation-entity [game-db position animation-k]
  (assert (keyword? animation-k))
  (let [{:keys [entity/animation]} (safe-get game-db animation-k)]
    (spawn-effect position
                  {:entity/animation (assoc animation :delete-after-stopped? true)})))

(defn spawn-effect* [v effect-ctx game-db fsms entity*]
  (let [source (:effect/source effect-ctx)
        position (:effect/target-position effect-ctx)]
    [[:tx/sound "bfxr_curse.wav"]
     [:tx/spawn-entity (entity* (animation-entity game-db position :audiovisuals/spawn))]
     [:tx/spawn-entity (entity* (spawn-creature {:position position
                                                 :creature-property v
                                                 :components {:entity/fsm {:fsm (:npc fsms)
                                                                           :initial-state :npc-idle}
                                                              :entity/faction (:entity/faction @source)}}))]]))

(defn target-all-effect [v effect-ctx affected-targets applicable-effect-txs entity*]
  (let [source (:effect/source effect-ctx)
        source* @source]
    (mapcat (fn [target]
              (cons [:tx/spawn-entity
                     (entity* (spawn-line {:start (:entity/position source*)
                                           :end (:entity/position @target)
                                           :duration 0.05
                                           :color (color/float-bits 1 0 0 0.75)
                                           :thick? true}))]
                    (applicable-effect-txs {:effect/source source
                                            :effect/target target}
                                           (:entity-effects v))))
            (affected-targets source*))))

(defn convert-effect [_v effect-ctx game-db entity*]
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)]
    [[:tx/sound "bfxr_burrow.wav"]
     [:tx/spawn-entity (entity* (animation-entity game-db (:entity/position @target) :audiovisuals/convert))]
     [:tx/swap target assoc :entity/faction (:entity/faction @source)]]))

(defn target-entity-effect [v effect-ctx game-db applicable-effect-txs entity*]
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)
        body        @source
        target-body @target
        {:keys [maxrange entity-effects]} v]
    (if (in-range? body target-body maxrange)
      (cons [:tx/spawn-entity
             (entity* (spawn-line
                        {:start (start-point body target-body)
                         :end (:entity/position target-body)
                         :duration 0.05
                         :color (color/float-bits 1 0 0 0.75)
                         :thick? true}))]
            (applicable-effect-txs effect-ctx entity-effects))
      [[:tx/sound "bfxr_fisthit.wav"]
       [:tx/spawn-entity (entity* (animation-entity game-db (end-point body target-body maxrange) :audiovisuals/hit-ground))]])))

(defn damage-effect [v effect-ctx game-db create-timer entity* kill-txs tx-event-txs]
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)
        source* @source
        target* @target
        hp (get-hitpoints (:entity/stats target*))]
    (cond
      (zero? (hp 0))
      nil

      (not (:entity/stats target*))
      nil

      (and (:entity/stats source*)
           (:entity/stats target*)
           (< (rand) (effective-armor-save (:entity/stats source*) (:entity/stats target*))))
      [[:tx/swap target add-text-effect "[WHITE]ARMOR" 0.3 create-timer]]

      :else
      (let [min-max (if (:entity/stats source*)  ; projectiles dont have ....
                      (:damage/min-max (calc-damage (:entity/stats source*)
                                                    (:entity/stats target*)
                                                    v))
                      (:damage/min-max v))
            dmg-amount (let [[lower upper] min-max]
                         (+ lower (rand-int (inc (- upper lower)))))

            new-hp-val (max (- (hp 0) dmg-amount)
                            0)
            dmg-text (str "[RED]" dmg-amount "[]")]
        (concat
          [[:tx/swap target assoc-in [:entity/stats :stats/hp 0] new-hp-val]
           [:tx/swap target add-text-effect dmg-text 0.3 create-timer]
           [:tx/sound "bfxr_normalhit.wav"]
           [:tx/spawn-entity (entity* (animation-entity game-db (:entity/position target*) :audiovisuals/damage))]]
          (if (zero? new-hp-val)
            (kill-txs target)
            (tx-event-txs target :alert)))))))

(defn kill-effect [_v effect-ctx kill-txs]
  (kill-txs (:effect/target effect-ctx)))

(defn spiderweb-effect [_v effect-ctx create-timer]
  (let [target (:effect/target effect-ctx)]
    ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
    (when-not (:entity/temp-modifier @target)
      [[:tx/swap target assoc :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                     :counter (create-timer spiderweb-duration)}]
       [:tx/swap target update :entity/stats add-mods spiderweb-modifiers]])))


; maybe the whole 'active-skill' or 'delayed-action'
; needs to be ONE data structure
; and projectile effects we do separate (first without schema/db later we see?)
; or even skill can do without schema/db?

; also lets make one 'type' for each as var for now?
; keys together

(defn effect-applicable? [[k v] effect-ctx]
  (case k
    :effects/projectile (:effect/target-direction effect-ctx)
    :effects/spawn (and (:entity/faction @(:effect/source effect-ctx))
                        (:effect/target-position effect-ctx))
    :effects/target-all true
    :effects/target-entity (and (:effect/target effect-ctx)
                                (seq (filter #(effect-applicable? % effect-ctx) (:entity-effects v))))
    :effects.target/convert (let [source (:effect/source effect-ctx)
                                  target (:effect/target effect-ctx)]
                              (and target
                                   (= (:entity/faction @target)
                                      (enemy (:entity/faction @source)))))
    :effects.target/damage (and (:effect/target effect-ctx)
                                #_(:stats/hp @target))
    :effects.target/kill (and (:effect/target effect-ctx)
                              (:entity/fsm @(:effect/target effect-ctx)))
    :effects.target/melee-damage (effect-applicable? [:effects.target/damage (melee-damage @(:effect/source effect-ctx))]
                                                     effect-ctx)
    :effects.target/spiderweb (:entity/stats @(:effect/target effect-ctx))
    :effects.target/stun (and (:effect/target effect-ctx)
                              (:entity/fsm @(:effect/target effect-ctx)))))

(defn effects->txs [foo** effect-ctx effects create-projectile game-db fsms entity* create-timer tx-event-txs kill-txs affected-targets*]
  (mapcat (fn [[k v]]
            (case k
              :effects/projectile (projectile-effect v effect-ctx create-projectile)
              :effects/spawn (spawn-effect* v effect-ctx game-db fsms entity*)
              :effects/target-all (target-all-effect v effect-ctx affected-targets* foo** entity*)
              :effects/target-entity (target-entity-effect v effect-ctx game-db foo** entity*)
              :effects.target/convert (convert-effect v effect-ctx game-db entity*)
              :effects.target/damage (damage-effect v effect-ctx game-db create-timer entity* kill-txs tx-event-txs)
              :effects.target/kill (kill-effect v effect-ctx kill-txs)
              :effects.target/melee-damage (damage-effect (melee-damage @(:effect/source effect-ctx)) effect-ctx game-db create-timer entity* kill-txs tx-event-txs)
              :effects.target/spiderweb (spiderweb-effect v effect-ctx create-timer)
              :effects.target/stun (let [duration v]
                                     (tx-event-txs (:effect/target effect-ctx) :stun duration))))
          (filter #(effect-applicable? % effect-ctx) effects)))

(defn effect-useful? [[k v] effect-ctx ray-blocked?]
  (case k
    :effects/projectile
    (let [{:keys [projectile/max-range] :as projectile} v
          source-p (:entity/position @(:effect/source effect-ctx))
          target-p (:entity/position @(:effect/target effect-ctx))]
      (and (not (let [[start1 target1 start2 target2] (double-ray-endpositions source-p
                                                                               target-p
                                                                               (:projectile/size projectile))]
                  (or
                    (ray-blocked? start1 target1)
                    (ray-blocked? start2 target2))))
           (< (v2-distance source-p target-p)
              max-range)))

    :effects/target-all
    false

    :effects/target-entity
    (in-range? @(:effect/source effect-ctx)
               @(:effect/target effect-ctx)
               (:maxrange v))

    true))

(defn effect-render! [[k v] effect-ctx shape-drawer active-entities raycaster]
  (case k
    :effects/target-all
    (let [source (:effect/source effect-ctx)
          source* @source]
      (doseq [target* (map deref (affected-targets active-entities raycaster source*))]
        (line! shape-drawer
               (:entity/position source*)
               (:entity/position target*)
               (color/float-bits 1 0 0 0.5))))

    :effects/target-entity
    (when-let [target (:effect/target effect-ctx)]
      (let [source (:effect/source effect-ctx)
            body        @source
            target-body @target
            maxrange (:maxrange v)]
        (line! shape-drawer
               (start-point body target-body)
               (end-point body target-body maxrange)
               (if (in-range? body target-body maxrange)
                 (color/float-bits 1 0 0 0.5)
                 (color/float-bits 1 1 0 0.5)))))

    nil))

(defn effects-render! [effects effect-ctx shape-drawer active-entities raycaster] 
  (doseq [effect effects]
    (effect-render! effect effect-ctx shape-drawer active-entities raycaster)))

(defn effects-applicable? [effects effect-ctx]
  (seq (filter #(effect-applicable? % effect-ctx) effects)))

(defn effects-useful? [effects effect-ctx raycaster-blocked?]
  (->> effects
       (filter (fn [e] (effect-applicable? e effect-ctx)))
       (some (fn [e] (effect-useful? e effect-ctx raycaster-blocked?)))))

(defn active-skill-draw
  [v entity shape-drawer batch unit-scale image->texture-region active-entities raycaster elapsed-time active-skill-radius]
  (let [{:keys [skill effect-ctx counter]} v
        {:keys [entity/image skill/effects]} skill
        radius active-skill-radius
        action-counter-ratio (timer-ratio counter elapsed-time)
        [x y] (:entity/position entity)
        y (+ (float y)
             (float (/ (:entity/height entity) 2))
             (float 0.15))
        center [x (+ y radius)]]
    (filled-circle! shape-drawer center radius (color/float-bits 1 1 1 0.125))
    (sector! shape-drawer
             center
             radius
             (math/to-radians 90)
             (math/to-radians (* (float action-counter-ratio) 360))
             (color/float-bits 1 1 1 0.5))
    (draw-texture-region! batch unit-scale (image->texture-region image) [(- (float x) radius) y])
    (effects-render! effects effect-ctx shape-drawer active-entities raycaster)))

(defn usable-state [{:keys [skill/cooling-down? skill/effects] :as skill} entity effect-ctx]
  (cond
    cooling-down?
    :cooldown

    (> (:skill/cost skill) ((get-mana (:entity/stats entity)) 0))
    :not-enough-mana

    (not (effects-applicable? effects effect-ctx))
    :invalid-params

    :else
    :usable))

(defn choose-skill [raycaster-blocked? entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (usable-state % entity effect-ctx))
                     (effects-useful? (:skill/effects %) effect-ctx raycaster-blocked?)))
       first))

(defn active-skill-tick
  [{:keys [skill effect-ctx counter]} 
   eid 
   line-of-sight? 
   applicable-effect-txs 
   elapsed-time 
   tx-event-txs]
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)
        effect-ctx (if (and target
                            (not (:entity/destroyed? @target))
                            (line-of-sight? @source @target))
                     effect-ctx
                     (dissoc effect-ctx :effect/target))]
    (cond
      (not (effects-applicable? (:skill/effects skill) effect-ctx))
      (tx-event-txs eid :action-done)

      (timer-stopped? counter elapsed-time)
      (concat (applicable-effect-txs effect-ctx (:skill/effects skill))
              (tx-event-txs eid :action-done)))))

(defn delete-after-duration-tick
  [v eid _delta-time elapsed-time]
  (when (timer-stopped? v elapsed-time)
    [[:tx/swap eid assoc :entity/destroyed? true]]))

(defn stunned-tick
  [v eid _delta-time elapsed-time tx-event-txs]
  (let [{:keys [counter]} v]
    (when (timer-stopped? counter elapsed-time)
      (tx-event-txs eid :effect-wears-off))))

(defn npc-sleeping-tick
  [_v eid _delta-time grid tx-event-txs]
  (let [entity @eid]
    (when-let [distance (nearest-enemy-distance grid entity)]
      (when (<= distance (get-value (:entity/stats entity) :stats/aggro-range))
        (tx-event-txs eid :alert)))))

(defn start-action-txs [eid skill effect-ctx create-timer tx-event-txs]
  (let [cost (:skill/cost skill)]
    (concat
      [[:tx/swap eid update :entity/stats (fn [stats]
                                            (let [mana-val ((get-mana stats) 0)]
                                              (assert (<= cost mana-val))
                                              (assoc-in stats [:stats/mana 0] (- mana-val cost))))]
       [:tx/swap eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] (create-timer (:skill/cooldown skill))]
       [:tx/sound (:skill/start-action-sound skill)]]
      (tx-event-txs eid :start-action [skill effect-ctx]))))

(defn npc-idle-tick
  [_v eid _delta-time line-of-sight? grid choose-skill create-timer tx-event-txs]
  (let [entity @eid
        target (nearest-enemy grid entity)
        target (when (and target
                          (line-of-sight? entity @target))
                 target)
        effect-ctx {:effect/source eid
                    :effect/target target
                    :effect/target-direction (when target
                                               (direction entity @target))}]
    (if-let [skill (choose-skill entity effect-ctx)]
      (start-action-txs eid skill effect-ctx create-timer tx-event-txs)
      (tx-event-txs eid :movement-direction (or (find-direction grid eid)
                                                [0 0])))))

(defn movement-tick
  [v eid delta-time grid max-speed]
  (let [{:keys [direction
                speed]
         :as movement} v]
    (assert (<= 0 speed max-speed)
            (pr-str speed))
    (assert (vector? direction))
    (assert (or (zero? (v2-length direction))
                (nearly-equal? 1 (v2-length direction)))
            (str "cannot understand direction: " (pr-str direction)))
    (when-not (or (zero? (v2-length direction))
                  (nil? speed)
                  (zero? speed))
      (when-let [body (try-move-solid-body grid @eid (:entity/id @eid) (assoc movement :delta-time delta-time))]
        [[:tx/swap eid assoc :entity/position (:entity/position body)]
         [:tx/update-position eid]]))))

(defn npc-moving-tick
  [v eid delta-time elapsed-time grid max-speed tx-event-txs]
  (concat (movement-tick v eid delta-time grid max-speed)
          (when (timer-stopped? (:timer v) elapsed-time)
            (tx-event-txs eid :timer-finished))))

(defn create-action-bar []
  (let [table (table/create)]
    (cell/pad! (table/defaults table) 2)
    (doto (table/add! table (doto (horizontal-group/create)
                              (horizontal-group/space! 2)
                              (horizontal-group/pad! 2) ; horizontal group pad not 'pad!' from cell
                              (actor/set-name! "moon.ui.action-bar.horizontal-group")
                              (actor/set-user-object! (doto (button-group/create)
                                                  (button-group/set-max-check-count! 1)
                                                  (button-group/set-min-check-count! 0)))))
      (cell/expand!)
      (cell/bottom!))
    (table/row! table)
    (doto table
      (layout/pack!)
      (table/set-fill-parent! true)
      (actor/set-name! "moon.ui.action-bar"))))

(defn destroy-entities
  [game-db entity-ids entity*]
  (mapcat (fn [eid]
            (cons [:tx/remove-entity eid]
                  (mapcat (fn [[k v]]
                            (case k
                              :entity/destroy-audiovisual
                              [[:tx/sound
                                (case v
                                  :audiovisuals/creature-die "bfxr_defaultmonsterdeath.wav"
                                  :audiovisuals/hit-wall "bfxr_projectile_wallhit.wav")]
                               [:tx/spawn-entity (entity* (animation-entity game-db (:entity/position @eid) v))]]
                              nil))
                          @eid)))
          (filter (comp :entity/destroyed? deref) (vals @entity-ids))))

(defn- movement-vector [key-pressed?]
  (let [r (when (key-pressed? :keys/d) [1  0])
        l (when (key-pressed? :keys/a) [-1 0])
        u (when (key-pressed? :keys/w) [0  1])
        d (when (key-pressed? :keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v2-normalise (reduce #(mapv + %1 %2) [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2-length v))
          v)))))

(defn selected-skill [action-bar]
  (let [horizontal-group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")
        button-group (actor/user-object horizontal-group)]
    (when-let [skill-button (button-group/checked button-group)]
      (actor/user-object skill-button))))

(defn validate-hum [schema value]
  (when-not (m/validate schema value)
    (throw (ex-info (str (me/humanize (m/explain schema value)))
                    {:value value
                     :schema (m/form schema)}))))
(defn malli-form [schemas schema]
  (case (first schema)
    :s/animation
    [:map {:closed true}
     [:animation/frames :some]
     [:animation/frame-duration pos?]
     [:animation/looping? :boolean]]

    :s/boolean
    :boolean

    :s/enum
    (apply vector :enum (rest schema))

    :s/image
    [:map {:closed true}
     [:image/file :string]
     [:image/bounds {:optional true} [:vector {:size 4} nat-int?]]]

    :s/map
    (let [ks (second schema)] 
      (apply vector :map {:closed true}
             (for [k ks
                   :let [k? (keyword? k)
                         schema-props (if k? nil (k 1))
                         k (if k? k (k 0))]]
               (do
                 (assert (keyword? k))
                 (assert (or (nil? schema-props) (map? schema-props)) (pr-str ks))
                 [k schema-props (malli-form schemas (get schemas k))]))))

    :s/number
    (case (second schema)
      :int     int?
      :nat-int nat-int?
      :any     number?
      :pos     pos?
      :pos-int pos-int?)

    :s/one-to-many
    [:set [:qualified-keyword {:namespace (keyword (name (second schema)))}]]

    :s/one-to-one
    [:qualified-keyword {:namespace (keyword (name (second schema)))}]

    :s/qualified-keyword
    (apply vector :qualified-keyword (rest schema))

    :s/some
    :some

    :s/sound
    :string

    :s/string
    :string

    :s/val-max
    val-max-schema
    ))
(defn malli-schema [schemas k]
  (m/schema (malli-form schemas (get schemas k))))
(defn create-db [schemas properties]
  (assert (or (empty? properties)
              (apply distinct? (map :property/id properties))))
  (doseq [property properties
          :let [k (keyword "properties" (namespace (:property/id property)))]]
    (validate-hum (malli-schema schemas k) property))
  (zipmap (map :property/id properties) properties))

(defn update-xy! [camera [x y]]
  (let [pos (orthographic-camera/position camera)]
    (vector3/set-x! pos x)
    (vector3/set-y! pos y)
    (orthographic-camera/update! camera)))

(defn set-camera-position! [viewport xy]
  (update-xy! (viewport/camera viewport) xy))

(defn tx-event-txs* 
  [create-entity-state entity-state-exit! eid event & [params]]
  (let [fsm (:entity/fsm @eid)
        _ (assert fsm (str "entity doesnt have fsm - keys: " (keys @eid)))
        old-state-k (:state fsm)
        new-fsm (fsm/fsm-event fsm event)
        new-state-k (:state new-fsm)]
    (when-not (= old-state-k new-state-k)
      (let [old-state-obj (let [k (:state (:entity/fsm @eid))] 
                            [k (k @eid)])
            state-args (if params 
                         [new-state-k params]
                         [new-state-k nil])
            new-state-obj [new-state-k (create-entity-state state-args @eid)]]
        (concat [[:tx/swap eid assoc :entity/fsm new-fsm]
                 [:tx/swap eid assoc new-state-k (new-state-obj 1)]
                 [:tx/swap eid dissoc old-state-k]]
                (entity-state-exit! eid old-state-obj))))))

(defn spawn-entity! [entity-ids content-grid grid entity]
  (let [id (:entity/id entity)
        eid (atom entity)]
    (assert (number? id))
    (assert (not (contains? @entity-ids id)))
    (swap! entity-ids assoc id eid)
    (assert (:entity/position @eid))
    (update-entity! content-grid eid)
    (assert (:entity/position @eid))
    (when (:entity/collides? @eid)
      (assert (valid-position? grid @eid id)))
    (set-touched-cells! grid eid)
    (when (:entity/collides? @eid)
      (set-occupied-cells! grid eid))
    eid))

(defn update-entity-position! [content-grid grid eid]
  (update-entity! content-grid eid)
  (remove-from-touched-cells! grid eid)
  (set-touched-cells! grid eid)
  (when (:entity/collides? @eid)
    (remove-from-occupied-cells! grid eid)
    (set-occupied-cells! grid eid)))

(defn remove-entity!* [entity-ids content-grid grid eid]
  (swap! entity-ids dissoc (:entity/id @eid))
  (remove-entity! content-grid eid)
  (remove-from-touched-cells! grid eid)
  (when (:entity/collides? @eid)
    (remove-from-occupied-cells! grid eid)))

(defn create-entity-state [[k v] entity create-timer]
  (case k
    :active-skill
    (let [[skill effect-ctx] v]
      {:skill skill
       :effect-ctx effect-ctx
       :counter (create-timer (/ (:skill/action-time skill)
                                 (or (get-value (:entity/stats entity)
                                                (:skill/action-time-modifier-key skill))
                                     1)))})

    :stunned
    {:counter (create-timer v)}

    :player-moving
    {:direction v
     :speed (or (get-value (:entity/stats entity) :stats/movement-speed)
                0)}

    :npc-moving
    {:direction v
     :speed (or (get-value (:entity/stats entity) :stats/movement-speed)
                0)
     :timer (create-timer (* (get-value (:entity/stats entity) :stats/reaction-time)
                             0.016))}

    v))

(defn construct-entity [entity create-timer minimum-size z-orders]
  (let [entity (reduce (fn [m [k v]]
                         (assoc m k (case k
                                      :entity/animation
                                      (create-animation v)

                                      :entity/delete-after-duration
                                      (create-timer v)

                                      :entity/projectile-collision
                                      (assoc v :already-hit-bodies #{})

                                      :entity/stats
                                      (-> v
                                          (update :stats/mana (fn [v] [v v]))
                                          (update :stats/hp   (fn [v] [v v])))

                                      v)))
                       {}
                       entity)]
    (let [{:entity/keys [position width height collides? z-order rotation-angle]} entity]
      (assert position)
      (assert width)
      (assert height)
      (assert (>= width  (if collides? minimum-size 0)))
      (assert (>= height (if collides? minimum-size 0)))
      (assert (or (boolean? collides?) (nil? collides?)))
      (assert ((set z-orders) z-order))
      (assert (or (nil? rotation-angle)
                  (<= 0 rotation-angle 360)))
      (assoc entity
             :entity/position (mapv float position)
             :entity/width (float width)
             :entity/height (float height)
             :entity/rotation-angle (or rotation-angle 0)))))

(defn entity-state-exit! [eid [k _v] create-timer entity*]
  (case k
    :npc-sleeping
    [[:tx/swap eid add-text-effect "[WHITE]!" 1 create-timer]
     [:tx/spawn-entity (entity* (spawn-effect (:entity/position @eid) 
                                              {:entity/alert-friendlies-after-duration
                                               {:counter (create-timer 0.2)
                                                :faction (:entity/faction @eid)}}))]]

    nil))

(defn kill-txs* [tx-event-txs eid]
  (concat (tx-event-txs eid :kill)
          (if (:entity/player? @eid)
            [[:tx/sound "bfxr_playerdeath.wav"]
             [:tx/modal {:title "YOU DIED - again!"
                         :text "Good luck next time!"
                         :button-text "OK"
                         :on-click (fn [])}]]
            [[:tx/swap eid assoc :entity/destroyed? true]])))

; small functions is the win ! just extract ! its all simple !
(defn interaction-state->txs [[k params] inventory-visible? player-eid entity* create-timer* tx-event-txs]
  (case k
    :interaction-state/mouseover-actor
    nil

    :interaction-state/drop-item-on-cursor
    (let [item params]
      [[:tx/swap player-eid dissoc :entity/item-on-cursor]
       [:tx/sound "bfxr_itemputground.wav"]
       [:tx/spawn-entity (entity* (spawn-item (:entity/position @player-eid) item))]])

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          [[:tx/toggle-inventory-visible]]

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
              (inventory-visible?)
              ; beacuse of this case destroy entities havs to run after paused
              ; otherwise game pauses and we just mark destryoted 
              ; maybe transactions shouldnt be allowed to return partial valid state
              ; then can destroy immediately?
              [[:tx/swap clicked-eid assoc :entity/destroyed? true]
               [:tx/swap player-eid assoc :entity/item-on-cursor item]
               [:tx/sound "bfxr_takeit.wav"]]

              (can-pickup-item? (:entity/inventory @player-eid) item)
              (do
                (assert (valid-item? item))
                (let [[cell cell-item] (can-pickup-item? (:entity/inventory @player-eid) item)]
                  (assert cell)
                  (assert (nil? cell-item))
                  [[:tx/swap clicked-eid assoc :entity/destroyed? true]
                   [:tx/sound "bfxr_pickup.wav"]
                   [:tx/swap player-eid set-item cell item]
                   [:tx/set-item cell item]]))

              :else
              [[:tx/sound "bfxr_denied.wav"]
               [:tx/show-message "Your Inventory is full"]])))
        [[:tx/sound "bfxr_denied.wav"]
         [:tx/show-message "Too far away"]]))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      (start-action-txs player-eid skill effect-ctx create-timer* tx-event-txs))

    :interaction-state.skill/not-usable
    (let [state params]
      [[:tx/sound "bfxr_denied.wav"]
       [:tx/show-message (case state
                           :cooldown "Skill is still on cooldown"
                           :not-enough-mana "Not enough mana"
                           :invalid-params "Cannot use this here")]])

    :interaction-state/no-skill-selected
    [[:tx/sound "bfxr_denied.wav"]
     [:tx/show-message "No selected skill"]]))

(defn get-cursor-key [player-eid interaction-state] ; doesnt handle item on cursor swap/place/etc. cases?
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-fn (get {
                        :player-dead :cursors/black-x
                        :active-skill :cursors/sandclock
                        :stunned :cursors/denied
                        :player-moving :cursors/walking
                        :player-idle (fn
                                       [eid]
                                       (let [[k params] interaction-state]
                                         (case k
                                           :interaction-state/mouseover-actor
                                           (let [[actor-type params] params
                                                 inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                                                                (let [inventory-slot params]
                                                                                  (item-at (:entity/inventory @eid) inventory-slot)))]
                                             (cond
                                               inventory-cell-with-item?
                                               :cursors/hand-before-grab

                                               (= actor-type :mouseover-actor/window-title-bar)
                                               :cursors/move-window

                                               (= actor-type :mouseover-actor/button)
                                               :cursors/over-button

                                               (= actor-type :mouseover-actor/unspecified)
                                               :cursors/default

                                               :else
                                               :cursors/default))

                                           :interaction-state/drop-item-on-cursor
                                           :cursors/hand-grab ; FIXME better cursor ? or no different cursor?
                                           ; text what is current interaction state?

                                           :interaction-state/clickable-mouseover-eid
                                           (let [{:keys [clicked-eid
                                                         in-click-range?]} params]
                                             (case (:type (:entity/clickable @clicked-eid))
                                               :clickable/item (if in-click-range?
                                                                 :cursors/hand-before-grab
                                                                 :cursors/hand-before-grab-gray)
                                               :clickable/player :cursors/bag))

                                           :interaction-state.skill/usable
                                           :cursors/use-skill

                                           :interaction-state.skill/not-usable
                                           :cursors/skill-not-usable

                                           :interaction-state/no-skill-selected
                                           :cursors/no-skill-selected)))}
                       state-k)]
    (if (keyword? cursor-fn)
      cursor-fn
      (cursor-fn eid))))

(defn tick-component-txs [[k v] eid delta-time elapsed-time grid tx-event-txs mk-effect-txs max-speed create-timer* choose-skill line-of-sight?*]
  (case k
    :entity/animation [[:tx/swap eid animation-tick delta-time]]
    :entity/alert-friendlies-after-duration (alert-friendlies-after-duration-tick v eid delta-time elapsed-time grid tx-event-txs)
    :entity/string-effect (string-effect-tick v eid delta-time elapsed-time)
    :entity/skills (skills-tick v eid delta-time elapsed-time)
    :entity/temp-modifier (temp-modifier-tick v eid delta-time elapsed-time)
    :entity/projectile-collision (projectile-collision-tick v eid delta-time grid mk-effect-txs)
    :active-skill (active-skill-tick v eid line-of-sight?* mk-effect-txs elapsed-time tx-event-txs)
    :entity/delete-after-duration (delete-after-duration-tick v eid delta-time elapsed-time)
    :stunned (stunned-tick v eid delta-time elapsed-time tx-event-txs)
    :player-moving (movement-tick v eid delta-time grid max-speed)
    :npc-moving (npc-moving-tick v eid delta-time elapsed-time grid max-speed tx-event-txs)
    :npc-sleeping (npc-sleeping-tick v eid delta-time grid tx-event-txs)
    :npc-idle (npc-idle-tick v eid delta-time line-of-sight?* grid choose-skill create-timer* tx-event-txs)
    nil))

(defn create-interaction-state [mouseover-actor-info world-mouse-position get-selected-skill player-eid mouseover-eid]
  (let [item-on-cursor (:entity/item-on-cursor @player-eid)]
    (cond
      mouseover-actor-info
      [:interaction-state/mouseover-actor mouseover-actor-info]

      item-on-cursor
      [:interaction-state/drop-item-on-cursor item-on-cursor]

      (and mouseover-eid
           (:entity/clickable @mouseover-eid))
      [:interaction-state/clickable-mouseover-eid
       {:clicked-eid mouseover-eid
        :in-click-range? (< (v2-distance (:entity/position @player-eid)
                                         (:entity/position @mouseover-eid))
                            (:entity/click-distance-tiles @player-eid))}]

      :else
      (if-let [skill-id (get-selected-skill)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              target-position (or (and mouseover-eid
                                       (:entity/position @mouseover-eid))
                                  world-mouse-position)
              effect-ctx {:effect/source player-eid
                          :effect/target mouseover-eid
                          :effect/target-position target-position
                          :effect/target-direction (v2-direction (:entity/position @player-eid)
                                                                 target-position)}
              state (usable-state skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn handle-player-input [player-eid interaction-state inventory-visible? key-pressed? button-just-pressed? tx-event-txs entity* create-timer*]
  (case (:state (:entity/fsm @player-eid))
    :player-idle
    (if-let [move-v (movement-vector key-pressed?)]
      (tx-event-txs player-eid :movement-input move-v)
      (when (button-just-pressed? :buttons/left)
        (interaction-state->txs interaction-state inventory-visible? player-eid entity* create-timer* tx-event-txs)))

    :player-moving
    (if-let [move-v (movement-vector key-pressed?)]
      [[:tx/swap player-eid assoc :player-moving {:direction move-v
                                                  :speed (or (get-value (:entity/stats @player-eid) :stats/movement-speed)
                                                             0)}]]
      (tx-event-txs player-eid :no-movement-input))
    nil))

(defn primary-interaction! 
  [handle-txs!
   player-eid
   mouseover-eid
   tx-event-txs
   entity*
   create-timer*
   {:keys [set-cursor!
           get-selected-skill
           mouseover-actor-info
           world-mouse-position
           inventory-visible?
           key-pressed?
           button-just-pressed?]
    :as capabilities}]
  (let [interaction-state (create-interaction-state (mouseover-actor-info) 
                                                    (world-mouse-position) 
                                                    get-selected-skill
                                                    player-eid
                                                    mouseover-eid)
        cursor-key (get-cursor-key player-eid interaction-state)
        txs (handle-player-input player-eid
                                 interaction-state
                                 inventory-visible?
                                 key-pressed?
                                 button-just-pressed?
                                 tx-event-txs
                                 entity* 
                                 create-timer*)]
    (set-cursor! cursor-key)
    (handle-txs! capabilities txs)))

(defn message-counter-update! [player-message message-duration-seconds delta-time]
  (when (:text @player-message)
    (swap! player-message update :counter + delta-time)
    (when (>= (:counter @player-message) message-duration-seconds)
      (reset! player-message nil))))

(defn update-paused-state! [pausing? paused? state->pause-game? controls player-entity key-just-pressed? key-pressed?]
  (reset! paused?
          (or #_error
              (and pausing?
                   (state->pause-game? (:state (:entity/fsm player-entity)))
                   (not (or (key-just-pressed? (:unpause-once controls))
                            (key-pressed? (:unpause-continously controls))))))))

(defn key-pressed-checks! [controls viewport key-pressed?]
  (doseq [[k f] {(:zoom-in controls) (fn []
                                       (let [camera (viewport/camera viewport)]
                                         (orthographic-camera/set-zoom! camera (max 0.1 (+ (orthographic-camera/zoom camera) 0.025)))
                                         (orthographic-camera/update! camera)))
                 (:zoom-out controls) (fn []
                                        (let [camera (viewport/camera viewport)]
                                          (orthographic-camera/set-zoom! camera (max 0.1 (+ (orthographic-camera/zoom camera) -0.025)))
                                          (orthographic-camera/update! camera)))}]
    (when (key-pressed? k)
      (f))))

(defn key-just-pressed-checks! [controls stage key-just-pressed?]
  (doseq [[k f] {(:close-windows-key controls) (fn []
                                                 (close-windows! stage))
                 (:toggle-inventory controls) (fn []
                                                (toggle-inventory-visible! stage))
                 (:toggle-entity-info controls) (fn []
                                                  (toggle-entity-info! stage))}]
    (when (key-just-pressed? k)
      (f))))

(defn draw-component! 
  [active-entities 
   raycaster 
   elapsed-time 
   active-skill-radius 
   player-entity 
   image->texture-region 
   unit-scale 
   batch 
   default-font 
   shape-drawer 
   k 
   v 
   entity]
  (case k
    :entity/clickable
    (clickable-draw v entity batch default-font unit-scale)
    :entity/animation
    (animation-draw v entity batch unit-scale image->texture-region)
    :entity/image
    (image-draw v entity batch unit-scale image->texture-region)
    :entity/line-render
    (line-render-draw v entity shape-drawer)
    :entity/mouseover?
    (mouseover?-draw entity shape-drawer player-entity)
    :entity/stats
    (stats-draw entity shape-drawer unit-scale)
    :entity/string-effect
    (string-effect-draw v entity batch default-font unit-scale)
    :entity/temp-modifier
    (temp-modifier-draw entity shape-drawer)
    :active-skill
    (active-skill-draw v entity shape-drawer batch unit-scale image->texture-region active-entities raycaster elapsed-time active-skill-radius)
    :npc-sleeping
    (npc-sleeping-draw entity batch default-font unit-scale)
    :stunned
    (stunned-draw entity shape-drawer)))

(defn update-mouseover-eid! [render-z-order raycaster grid player-eid mouseover-eid mouseover-actor world-mouse-position]
  (let [old-mouseover-eid @mouseover-eid
        new-eid (if mouseover-actor
                  nil
                  (let [player @(player-eid)
                        hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                     (point->entities @grid world-mouse-position))]
                    (->> hits
                         (sort-by #((:entity/z-order @%) render-z-order) <)
                         reverse
                         (filter #(line-of-sight? @raycaster player @%))
                         first)))]
    (when old-mouseover-eid
      (swap! old-mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (reset! mouseover-eid new-eid)))

(defn update-active-entities! [active-entities content-grid player-eid]
  (reset! active-entities (active-entities* @content-grid @(player-eid))))

(defn game-menus [show-tile-grid?
                  show-cell-entities?
                  show-cell-occupied?
                  show-body-bounds?
                  show-potential-field-colors?
                  show-potential-field-colors?
                  controls-info]
  [{:label "Debug"
    :items [{:label "Toggle show-tile-grid?"
             :on-click #(swap! show-tile-grid? not)}
            {:label "Toggle show-cell-entities?"
             :on-click #(swap! show-cell-entities? not)}
            {:label "Toggle show-cell-occupied?"
             :on-click #(swap! show-cell-occupied? not)}
            {:label "Toggle show-body-bounds?"
             :on-click #(swap! show-body-bounds? not)}
            {:label "Potential field colors: off"
             :on-click #(reset! show-potential-field-colors? nil)}
            {:label "Potential field colors: :good"
             :on-click #(reset! show-potential-field-colors? :good)}
            {:label "Potential field colors: :evil"
             :on-click #(reset! show-potential-field-colors? :evil)}]}
   {:label "Help"
    :items [{:label controls-info}]}])

(defn when-not-paused-updates!* 
  [paused? 
   max-delta
   elapsed-time
   factions-iterations
   grid
   potential-field-cache
   active-entities
   tx-event-txs 
   mk-effect-txs
   max-speed
   create-timer*
   choose-skill
   line-of-sight?*
   delta-time]
  (when-not @paused?
    (let [delta-time (min delta-time max-delta)]
      (swap! elapsed-time + delta-time)
      (doseq [[faction max-iterations] factions-iterations]
        (update! @grid
                 potential-field-cache
                 faction
                 @active-entities
                 max-iterations))
      (mapcat (fn [eid] 
                (mapcat (fn [component]
                          (tick-component-txs component 
                                              eid 
                                              delta-time 
                                              @elapsed-time 
                                              @grid 
                                              tx-event-txs 
                                              mk-effect-txs
                                              max-speed
                                              create-timer*
                                              choose-skill
                                              line-of-sight?*)) 
                        @eid))
              @active-entities))))

(defn game-update-labels [frames-per-second timer-info mouseover-eid paused? ui-mouse-info-text get-input-position unproject-viewport camera-zoom] 
  [{:label "elapsed-time"
    :update-fn timer-info
    :icon "images/clock.png"}
   {:label "FPS"
    :update-fn frames-per-second
    :icon "images/fps.png"}
   {:label "Mouseover-entity id"
    :update-fn (fn []
                 (when-let [entity (and @mouseover-eid @@mouseover-eid)]
                   (:entity/id entity)))
    :icon "images/mouseover.png"}
   {:label "paused?"
    :update-fn (fn [] @paused?)}
   {:label "GUI"
    :update-fn ui-mouse-info-text}
   {:label "World"
    :update-fn (fn []
                 (mapv int (unproject-viewport (get-input-position))))}
   {:label "Zoom"
    :update-fn camera-zoom 
    :icon "images/zoom.png"}])

(defn draw-bar! [rahmenw rahmenh rahmen-file default-font image->texture-region get-unit-scale batch x y content-file minmaxval name]
  (draw-texture-region! batch
                        (get-unit-scale)
                        (image->texture-region {:image/file rahmen-file})
                        [x y])
  (draw-texture-region! batch
                        (get-unit-scale)
                        (image->texture-region
                          {:image/file content-file
                           :image/bounds [0 0 (* rahmenw (val-max-ratio minmaxval)) rahmenh]})
                        [x y])
  (draw-text! batch
              default-font
              (get-unit-scale)
              {:text (str (readable (minmaxval 0))
                          "/"
                          (minmaxval 1)
                          " "
                          name)
               :x (+ x 75)
               :y (+ y 2)
               :up? true}))

(defn draw-world-unit-scale! 
  [[batch
    default-font
    shape-drawer 
    world-unit-scale 
    unit-scale 
    viewport
    show-tile-grid?
    factions-iterations
    show-cell-entities?
    show-cell-occupied?
    show-potential-field-colors?
    grid
    active-entities
    raycaster
    elapsed-time
    active-skill-radius
    player-eid
    image->texture-region
    show-body-bounds?
    render-z-order
    render-layers
    get-input-position]]
  (with-line-width shape-drawer world-unit-scale
    (reset! unit-scale world-unit-scale)
    (when @show-tile-grid?
      (draw-tile-grid shape-drawer viewport))
    (draw-cell-debug factions-iterations
                     shape-drawer 
                     viewport
                     @show-cell-entities?
                     @show-cell-occupied?
                     @show-potential-field-colors?
                     @grid)
    (draw-entities! (fn [& params]
                      (apply draw-component! 
                             @active-entities 
                             @raycaster 
                             @elapsed-time 
                             active-skill-radius
                             @(player-eid)
                             image->texture-region 
                             @unit-scale 
                             batch 
                             default-font 
                             shape-drawer 
                             params))
                    shape-drawer
                    @raycaster
                    @active-entities
                    @(player-eid)
                    @show-body-bounds?
                    render-z-order
                    render-layers)
    (highlight-mouseover-tile shape-drawer (unproject viewport (get-input-position)) @grid)
    (reset! unit-scale 1)))

(defn game-listener 
  [{:keys [game-db
           build-property
           info-text
           pausing?
           state->pause-game?
           controls
           world-unit-scale
           slot->y-sprite-idx
           controls-info 
           max-delta
           max-speed
           fsms
           inventory-cell-size
           slot->texture-region
           factions-iterations
           construct-entity
           create-level!
           render-z-order
           render-layers
           font-params
           y-mana
           rahmenw
           rahmenh
           y-hp
           rahmen-file
           hpcontent-file
           manacontent-file
           ]
    :as game-config}]
  (let [
        active-skill-radius (let [tile-size 48
                                  image-width 32]
                              (/ (/ image-width tile-size) 2))

        elapsed-time (atom 0)
        create-timer* (fn [duration]
                        (timer-create duration @elapsed-time))
        timer-info (fn [] 
                     (str (readable @elapsed-time) " seconds"))

        id-counter (atom 0)

        generate-id!* #(swap! id-counter inc)

        entity* (fn
                  ([entity]
                   (assoc (construct-entity entity create-timer*)
                          :entity/id (generate-id!*)))
                  ([entity id]
                   (assoc (construct-entity entity create-timer*)
                          :entity/id id)))

        create-projectile (fn [foo bar]
                            (entity* (spawn-projectile foo bar)))
        create-entity-state (fn [state-args entity]
                                (create-entity-state state-args entity create-timer*))
        entity-state-exit! (fn [eid old-state-obj]
                             (entity-state-exit! eid old-state-obj create-timer* entity*))
        tx-event-txs (fn [& params]
                       (apply tx-event-txs* create-entity-state entity-state-exit! params))
        kill-txs (fn [eid]
                   (kill-txs* tx-event-txs eid))
        raycaster (atom nil)
        active-entities (atom nil)
        affected-targets* (fn [entity]
                            (affected-targets @active-entities @raycaster entity))
        mk-effect-txs (fn foo** [effect-ctx effects]
                        (effects->txs foo** effect-ctx effects create-projectile game-db fsms entity* create-timer* tx-event-txs kill-txs affected-targets*))
        choose-skill (fn [& params]
                       (apply choose-skill (partial raycaster-blocked? @raycaster) params))
        grid (atom nil)
        line-of-sight?* (fn [& params]
                         (apply line-of-sight? @raycaster params))
        entity-ids (atom {})
        player-id (atom nil)
        player-eid (fn []
                     (let [eid (get @entity-ids @player-id)]
                       (assert eid (str "player id not in entity-ids: " @player-id))
                       eid))
        mouseover-eid (atom nil)
        player-message (atom nil)
        message-duration-seconds 0.5
        update-message-counter! (fn [{:keys [get-delta-time]}]
                                  (message-counter-update! player-message message-duration-seconds (get-delta-time)))
        paused? (atom false)
        explored-tile-corners (atom nil)
        tiled-map* (atom nil)
        content-grid (atom nil)
        show-potential-field-colors? (atom nil)
        show-cell-entities? (atom false)
        show-cell-occupied? (atom false)
        show-body-bounds? (atom false)
        show-tile-grid? (atom false)
        potential-field-cache (atom nil)
        state (atom nil)
        ]
    {
     :create!
     (fn [{:keys [clear-screen!
                  get-delta-time
                  key-pressed?
                  key-just-pressed? 
                  button-just-pressed?
                  set-cursor!
                  internal
                  get-input-position 
                  set-input-processor! 
                  new-sound
                  frames-per-second
                  new-cursor]}]
       (tooltip-manager/set-initial-time! (tooltip-manager/instance) 0)
       (let [
             
             textures (:textures game-config)
             skin (:skin game-config)
             sounds (:sounds game-config)
             stage (:stage game-config)
             cursors (:cursors game-config)

             batch (sprite-batch/create)
             textures (create-textures internal textures)
             default-font (create-default-font! internal font-params)
             shape-drawer-texture (white-pixel-texture)
             skin (create-skin! (internal skin))
             sounds (into {} 
                          (for [path sounds] 
                            [path (new-sound (internal path))]))
             play-sound! (fn [sound-name]
                            (sound/play! (safe-get sounds sound-name)))
             stage (stage/create (fit-viewport/create (:width stage) (:height stage))
                                 batch)
             image->texture-region #(get-texture-region textures %)
             cursors (update-vals cursors
                                  (fn [[path [hotspot-x hotspot-y]]]
                                    (let [pixmap (pixmap/create (internal path))
                                          cursor (new-cursor pixmap hotspot-x hotspot-y)]
                                      (disposable/dispose! pixmap)
                                      cursor)))
             get-mouseover-actor #(let [[x y] (unproject (stage/viewport stage) (get-input-position))]
                                    (stage/hit stage x y))
             viewport (create-viewport! 1440 900 world-unit-scale)
             capabilities {
                           :get-delta-time get-delta-time
                           :key-pressed? key-pressed?
                           :button-just-pressed? button-just-pressed?
                           :inventory-visible? #(-> (stage/root stage)
                                                    (group/find-actor "moon.ui.windows.inventory")
                                                    actor/visible?)
                           :mouseover-actor-info #(if-let [actor (get-mouseover-actor)]
                                                    (mouseover-actor-info actor)
                                                    nil)
                           :world-mouse-position #(unproject viewport (get-input-position))
                           :set-cursor! #(set-cursor! (safe-get cursors %)) 
                           :get-selected-skill #(-> (stage/root stage)
                                                    (group/find-actor "moon.ui.action-bar")
                                                    selected-skill)
                           :play-sound! play-sound!
                           :show-modal! (fn [opts]
                                          (show-modal! skin stage opts))
                           :set-item! (fn [cell image tooltip-text]
                                        (-> (stage/root stage)
                                            (group/find-actor "moon.ui.windows.inventory")
                                            (set-item!* cell
                                                        {:texture-region (image->texture-region image)
                                                         :tooltip-text tooltip-text}
                                                        skin)))

                           :remove-item! (fn [cell] 
                                           (remove-item! stage cell))
                           :toggle-inventory-visible! (fn []
                                                        (toggle-inventory-visible! stage))
                           :set-skill! (fn [skill-id image tooltip-text]
                                         (-> (stage/root stage)
                                             (group/find-actor "moon.ui.action-bar")
                                             (add-skill! {:skill-id skill-id
                                                          :texture-region (image->texture-region image)
                                                          :tooltip-text tooltip-text}
                                                         skin)))
                           }
             shape-drawer (shape-drawer/create batch (texture-region/create shape-drawer-texture 1 0 1 1))
             unit-scale (atom 1)
             get-unit-scale (fn [] @unit-scale)
             unproject-viewport #(unproject viewport %)
             camera-zoom (fn [] (orthographic-camera/zoom (viewport/camera viewport)))
             ui-viewport-width (viewport/world-width (stage/viewport stage))
             ui-viewport-height (viewport/world-height (stage/viewport stage))
             ui-mouse-info-text (fn []
                                  (mapv int (unproject (stage/viewport stage) (get-input-position))))
             slot->texture-region* (fn [slot] (image->texture-region (slot->texture-region slot)))
             update-labels (map (fn [item]
                                  (if (:icon item)
                                    (update item :icon #(get textures %))
                                    item))
                                (game-update-labels frames-per-second timer-info mouseover-eid paused? ui-mouse-info-text get-input-position unproject-viewport camera-zoom))
             info-window-opts {:title "Entity Info"
                               :actor-name "moon.ui.windows.entity-info"
                               :visible? false
                               :position [ui-viewport-width 0]
                               :set-label-text! (fn []
                                                  (if-let [eid @mouseover-eid]
                                                    (info-text (apply dissoc @eid [:entity/skills
                                                                                   :entity/faction
                                                                                   :active-skill])
                                                               @elapsed-time)
                                                    ""))
                               :skin skin}
             handle-txs! (fn [{:keys [play-sound!
                                      show-modal!
                                      set-item!
                                      remove-item!
                                      toggle-inventory-visible!
                                      set-skill!]} 
                              txs]
                           (let [k->txs {
                                         :tx/swap swap!
                                         :tx/spawn-entity 
                                         (fn [entity]
                                           (spawn-entity! entity-ids @content-grid @grid entity))
                                         :tx/update-position
                                         (fn [eid]
                                           (update-entity-position! @content-grid @grid eid))
                                         :tx/remove-entity
                                         (fn [eid]
                                           (remove-entity!* entity-ids @content-grid @grid eid))
                                         :tx/modal show-modal!
                                         :tx/set-item 
                                         (fn [cell item]
                                           (set-item! cell (:entity/image item) (item-info-text item)))
                                         :tx/sound play-sound! 
                                         :tx/remove-item remove-item!
                                         :tx/toggle-inventory-visible toggle-inventory-visible!
                                         :tx/show-message
                                         (fn [message]
                                           (reset! player-message {:text message :counter 0}))
                                         :tx/set-skill set-skill!
                                         }]
                             (doseq [[k & params] txs]
                               (try (apply (safe-get k->txs k) params)
                                    (catch Throwable t
                                      (throw (ex-info "handle-txs! error" 
                                                      {:k k :params params}
                                                      t)))))))
             inventory-window-opts {:get-mouse-position get-input-position
                                    :on-click-cell (fn [_event cell]
                                                     (handle-txs! capabilities (handle-clicked-inventory-cell (player-eid) cell)))
                                    :draw-cell-rect! (fn [x y mouseover? cell]
                                                       (rectangle! shape-drawer x y inventory-cell-size inventory-cell-size (color/float-bits 0.5 0.5 0.5 1))
                                                       (let [player-entity @(player-eid)]
                                                         (when (and mouseover? (:entity/item-on-cursor player-entity))
                                                           (let [item (:entity/item-on-cursor player-entity)
                                                                 color (if (valid-slot? cell item)
                                                                         (color/float-bits 0 0.6 0 0.8)
                                                                         (color/float-bits 0.6 0 0 0.8))]
                                                             (filled-rectangle! shape-drawer (inc x) (inc y) (- inventory-cell-size 2) (- inventory-cell-size 2) color)))))
                                    :skin skin
                                    :position [ui-viewport-width ui-viewport-height]
                                    :slot->texture-region slot->texture-region*
                                    :cell-size inventory-cell-size}
             draw-item-on-cursor! (fn []
                                    (when-let [item (:entity/item-on-cursor @(player-eid))]
                                      (draw-texture-region! batch
                                                            (get-unit-scale)
                                                            (image->texture-region (:entity/image item))
                                                            (unproject (stage/viewport stage) (get-input-position))
                                                            {:center? true})))
             draw-player-message! (fn []
                                    (when-let [text (:text @player-message)]
                                      (draw-text! batch
                                                  default-font
                                                  (get-unit-scale)
                                                  {:x (/ ui-viewport-width 2)
                                                   :y (+ (/ ui-viewport-height 2) 200)
                                                   :text text
                                                   :scale 2.5
                                                   :up? true})))
             draw-tiled-map!* (fn []
                                (draw-tiled-map! @tiled-map*
                                                 batch
                                                 (camera-view-bounds (viewport/camera viewport))
                                                 (tile-color-setter*
                                                   {:ray-blocked? (partial raycaster-blocked? @raycaster)
                                                    :explored-tile-corners explored-tile-corners
                                                    :light-position (let [pos (orthographic-camera/position (viewport/camera viewport))]
                                                                      [(vector3/x pos) (vector3/y pos) (vector3/z pos)])
                                                    :see-all-tiles? false
                                                    :explored-tile-color (color/float-bits 0.5 0.5 0.5 1)
                                                    :visible-tile-color (color/float-bits 1 1 1 1)
                                                    :invisible-tile-color (color/float-bits 0 0 0 1)})
                                                 world-unit-scale))
             ]
         (doseq [actor [(create-action-bar)
                        (create-dev-menu {:menus (game-menus show-tile-grid?
                                                             show-cell-entities?
                                                             show-cell-occupied?
                                                             show-body-bounds?
                                                             show-potential-field-colors?
                                                             show-potential-field-colors?
                                                             controls-info)
                                          :update-labels update-labels 
                                          :skin skin})
                        (actor/create {:act!
                                       (fn [_this _delta])
                                       :draw!
                                       (fn [_this batch _parent-alpha]
                                         (let [stats (:entity/stats @(player-eid))
                                               bar-x (- (/ ui-viewport-width 2) (/ rahmenw 2))]
                                           (draw-bar! rahmenw 
                                                      rahmenh 
                                                      rahmen-file 
                                                      default-font 
                                                      image->texture-region 
                                                      get-unit-scale 
                                                      batch 
                                                      bar-x 
                                                      y-hp 
                                                      hpcontent-file 
                                                      (get-hitpoints stats) 
                                                      "HP")
                                           (draw-bar! rahmenw 
                                                      rahmenh 
                                                      rahmen-file 
                                                      default-font 
                                                      image->texture-region 
                                                      get-unit-scale 
                                                      batch 
                                                      bar-x 
                                                      y-mana 
                                                      manacontent-file 
                                                      (get-mana stats)
                                                      "MP")))})
                        (doto (group/create)
                          (add-actors! [(create-info-window info-window-opts)
                                        (build-inventory-window inventory-window-opts)])
                          (actor/set-name! "moon.ui.windows"))
                        (actor/create {:act! (fn [_this _delta])
                                       :draw! (fn [_this _batch _parent-alpha]
                                                (draw-item-on-cursor!))})
                        (actor/create {:act! (fn [_this _delta])
                                       :draw! (fn [_this _batch _parent-alpha]
                                                (draw-player-message!))})]]
           (stage/add-actor! stage actor))
         (set-input-processor! stage)
         (let [{:keys [tiled-map
                       start-position]} (create-level! textures image->texture-region)]
           (reset! tiled-map* tiled-map)
           (reset! id-counter 0)
           (reset! entity-ids {})
           (let [props (tiled-map/properties tiled-map)
                 width (map-properties/get props "width")
                 height (map-properties/get props "height")
                 g (create-grid tiled-map)]
             (reset! grid g) 
             (reset! content-grid (create-content-grid width height 16))
             (reset! explored-tile-corners (mapv (fn [x] (mapv (fn [y] false)
                                                               (range height)))
                                                 (range width)))
             (reset! raycaster (create-raycaster g)))
           (reset! elapsed-time 0)
           (reset! player-id (generate-id!*))
           (let [entity (entity* (spawn-creature {:position (mapv (partial + 0.5) start-position)
                                                  :creature-property (build-property :creatures/vampire)
                                                  :components {:entity/fsm {:fsm (:player fsms)
                                                                            :initial-state :player-idle}
                                                               :entity/faction :good
                                                               :entity/player? true
                                                               :entity/free-skill-points 3
                                                               :entity/clickable {:type :clickable/player}
                                                               :entity/click-distance-tiles 1.5}})
                                 @player-id)]
             (handle-txs! capabilities 
                          (concat [[:tx/spawn-entity entity]]
                                  (player-ui-txs #(info-text % @elapsed-time) entity)
                                  (spawn-map-creatures build-property @tiled-map* start-position fsms entity*)))))
         (reset! state {:dispose! (fn []
                                    (disposable/dispose! batch)
                                    (run! disposable/dispose! (vals cursors))
                                    (disposable/dispose! default-font)
                                    (disposable/dispose! shape-drawer-texture)
                                    (disposable/dispose! skin)
                                    (run! disposable/dispose! (vals sounds))
                                    (run! disposable/dispose! (vals textures))
                                    (disposable/dispose! @tiled-map*))
                        :resize! (fn [width height]
                                   (viewport/update! (stage/viewport stage) width height true)
                                   (viewport/update! viewport width height false))
                        :render! (fn []
                                   (update-mouseover-eid! render-z-order 
                                                          raycaster 
                                                          grid 
                                                          player-eid 
                                                          mouseover-eid 
                                                          (get-mouseover-actor)
                                                          (unproject viewport (get-input-position)))
                                   (update-active-entities! active-entities content-grid player-eid)
                                   (set-camera-position! viewport (:entity/position @(player-eid)))
                                   (primary-interaction! handle-txs! 
                                                         (player-eid)
                                                         @mouseover-eid
                                                         tx-event-txs
                                                         entity*
                                                         create-timer*
                                                         capabilities)
                                   (update-paused-state! pausing? paused? state->pause-game? controls @(player-eid) key-just-pressed? key-pressed?)
                                   (handle-txs! capabilities (when-not-paused-updates!* paused?
                                                                                        max-delta
                                                                                        elapsed-time
                                                                                        factions-iterations
                                                                                        grid
                                                                                        potential-field-cache
                                                                                        active-entities
                                                                                        tx-event-txs 
                                                                                        mk-effect-txs
                                                                                        max-speed
                                                                                        create-timer*
                                                                                        choose-skill
                                                                                        line-of-sight?*
                                                                                        (get-delta-time)))
                                   (handle-txs! capabilities (destroy-entities game-db entity-ids entity*))
                                   (key-pressed-checks! controls viewport key-pressed?)
                                   (key-just-pressed-checks! controls stage key-just-pressed?)
                                   (update-message-counter! capabilities)
                                   (stage/act! stage)
                                   (clear-screen! 0 0 0 0)
                                   (batch/set-projection-matrix! batch (orthographic-camera/combined (viewport/camera viewport)))
                                   (batch/set-color! batch 1 1 1 1) ; hotfix for tooltips they set the color every creature flashes
                                   (batch/begin batch)
                                   (draw-tiled-map!*)
                                   (draw-world-unit-scale! [batch
                                                            default-font
                                                            shape-drawer 
                                                            world-unit-scale 
                                                            unit-scale 
                                                            viewport
                                                            show-tile-grid?
                                                            factions-iterations
                                                            show-cell-entities?
                                                            show-cell-occupied?
                                                            show-potential-field-colors?
                                                            grid
                                                            active-entities
                                                            raycaster
                                                            elapsed-time
                                                            active-skill-radius
                                                            player-eid
                                                            image->texture-region
                                                            show-body-bounds?
                                                            render-z-order
                                                            render-layers
                                                            get-input-position])
                                   (batch/end batch)
                                   (stage/draw! stage))})))

     :dispose!
     (fn []
       ((:dispose! @state)))

     :render!
     (fn [_]
       ((:render! @state)))

     :resize!
     (fn [width height]
       ((:resize! @state) width height))
     }))

(defn- text-button [skin label on-click!]
  (doto (text-button/create label skin)
    (actor/add-listener! (change-listener/create
                     (fn [_event _actor]
                       (on-click!))))))

(defn fit-to-size! [camera width height]
  (let [pos (orthographic-camera/position camera)
        px (float (/ width 2))
        py (float (/ height 2))
        x-diff (max (- px (float 0)) (- (float width) px))
        y-diff (max (- (float height) py) (- py (float 0)))
        vp-ratio-w (/ (* x-diff 2) (orthographic-camera/viewport-width camera))
        vp-ratio-h (/ (* y-diff 2) (orthographic-camera/viewport-height camera))]
    (vector3/set-x! pos px)
    (vector3/set-y! pos py)
    (orthographic-camera/set-zoom! camera (max vp-ratio-w vp-ratio-h))
    (orthographic-camera/update! camera)))

(defn adjust-camera! [viewport tiled-map]
  (fit-to-size! (viewport/camera viewport)
                (map-properties/get (tiled-map/properties tiled-map) "width")
                (map-properties/get (tiled-map/properties tiled-map) "height")))

(defn generate-level [db textures level-fn]
  (let [tiled-map (:tiled-map 
                    (level-fn {:level/creature-properties
                               (prepare-creature-tiles
                                 (all-raw db :properties/creatures)
                                 #(get-texture-region textures %))
                               :textures textures}))]
    (tiled-map-tile-layer/set-visible! (map-layers/get (tiled-map/layers tiled-map) "creatures") true)
    tiled-map))

(defn levelgen-listener []
  (let [db (create-db (-> "schema.edn" io/resource slurp edn/read-string) 
                      (-> "properties.edn" io/resource slurp edn/read-string))
        tile-size 48
        world-unit-scale (/ tile-size)
        world-width (* 1440 world-unit-scale)
        world-height (* 900 world-unit-scale)
        tiled-map-color-setter (constantly (color/float-bits color/white))
        state (atom nil)]
    {:create!
     (fn [{:keys [internal set-input-processor!]}]
       (let [batch (sprite-batch/create)
             skin (skin/create (internal "skin/uiskin.json"))
             textures (create-textures internal
                                       {:folder "resources/"
                                        :extensions #{"png" "bmp"}})
             viewport (fit-viewport/create world-width
                                           world-height
                                           (doto (orthographic-camera/create)
                                             (orthographic-camera/set-to-ortho! false
                                                                                world-width
                                                                                world-height)))
             
             current-tiled-map (atom nil)
             button-window (let [window (window/create "Edit" skin)] 
                             (doseq [[label level-fn] [["Vampire" create-vampire-level]
                                                       ["UF Caves" create-uf-caves]
                                                       ["Modules" create-modules-level]]]
                               (table/add! window (text-button skin 
                                                         (str "Generate " label)
                                                         (fn []
                                                           (disposable/dispose! @current-tiled-map)
                                                           (reset! current-tiled-map (generate-level db textures level-fn))
                                                           (adjust-camera! viewport @current-tiled-map))))
                               (table/row! window))
                             (layout/pack! window)
                             window)
             stage (doto (stage/create (fit-viewport/create 1440 900) batch)
                     (stage/add-actor! button-window))]
         (set-input-processor! stage) 
         (reset! current-tiled-map (generate-level db textures create-uf-caves)) 
         (adjust-camera! viewport @current-tiled-map)
         (reset! state {:batch batch
                        :skin skin
                        :stage stage
                        :textures textures
                        :current-tiled-map current-tiled-map
                        :viewport viewport}))) 

     :dispose!
     (fn []
       (let [{:keys [batch skin textures current-tiled-map]} @state]
         (disposable/dispose! batch)
         (disposable/dispose! skin)
         (run! disposable/dispose! (vals textures))
         (disposable/dispose! @current-tiled-map)))

     :render!
     (fn [{:keys [clear-screen! key-pressed?]}]
       (let [{:keys [batch stage current-tiled-map viewport]} @state]
         (clear-screen! 0 0 0 0) 
         (batch/set-projection-matrix! batch (orthographic-camera/combined (viewport/camera viewport)))
         (batch/begin batch)
         (draw-tiled-map! @current-tiled-map
                          batch
                          (camera-view-bounds (viewport/camera viewport))
                          tiled-map-color-setter
                          world-unit-scale)
         (batch/end batch)
         (doseq [[k f] {:keys/minus (fn [camera]
                                      (orthographic-camera/set-zoom! camera (max 0.1 (+ (orthographic-camera/zoom camera) 0.1)))
                                      (orthographic-camera/update! camera))
                        :keys/equals (fn [camera]
                                       (orthographic-camera/set-zoom! camera (max 0.1 (+ (orthographic-camera/zoom camera) -0.1)))
                                       (orthographic-camera/update! camera))
                        :keys/left (fn [camera]
                                     (let [pos (orthographic-camera/position camera)]
                                       (vector3/set-x! pos (- (vector3/x pos) 1))
                                       (orthographic-camera/update! camera)))
                        :keys/right (fn [camera]
                                      (let [pos (orthographic-camera/position camera)]
                                        (vector3/set-x! pos (+ (vector3/x pos) 1))
                                        (orthographic-camera/update! camera)))
                        :keys/up (fn [camera]
                                   (let [pos (orthographic-camera/position camera)]
                                     (vector3/set-y! pos (+ (vector3/y pos) 1))
                                     (orthographic-camera/update! camera)))
                        :keys/down (fn [camera]
                                     (let [pos (orthographic-camera/position camera)]
                                       (vector3/set-y! pos (- (vector3/y pos) 1))
                                       (orthographic-camera/update! camera)))}]
           (when (key-pressed? k)
             (f (viewport/camera viewport))))
         (stage/act! stage)
         (stage/draw! stage)))

     :resize!
     (fn [width height]
       (let [{:keys [stage viewport]} @state]
         (viewport/update! (stage/viewport stage) width height true)
         (viewport/update! viewport width height false)))
     }))

(defn map-form-k->properties
  "Given a map schema gives a map of key to key properties (like :optional)."
  [map-schema]
  (let [[_m _p & ks] map-schema]
    (into {} (for [[k m? _schema] ks]
               [k (if (map? m?) m?)]))))

(comment
 (= (map-form-k->properties
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    {:foo nil,
     :bar nil,
     :baz {:optional true},
     :boz {:optional false},
     :asdf {:optional true}}))

(defn map-keys [map-schema]
  (let [[_m _p & ks] map-schema]
    (for [[k m? _schema] ks]
      k)))

(comment
 (= (map-keys
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    [:foo :bar :baz :boz :asdf]))

(defn optional? [map-schema k]
  (:optional (k (map-form-k->properties map-schema))))

(comment
 (= (optional?
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]]
     :foo)
    nil)

 (= (optional?
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]]
     :baz)
    true)

 (= (optional?
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]]
     :asdf)
    true))

(defn optional-keyset [map-schema]
  (set (filter #(optional? map-schema %)
               (map-keys map-schema))))

(comment
 (= (optional-keyset
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    #{:baz :asdf}))

(defn property-overview-rows 
  [property-type clicked-id-fn {:keys [editor/all-raw
                                       editor/get-texture-region
                                       editor/property-type->overview-table-props
                                       editor/text-tooltip
                                       editor/new-label]}]
  (let [{:keys [sort-by-fn
                extra-info-text
                columns
                image-scale]} (get property-type->overview-table-props property-type)]
    (for [row (->> (all-raw property-type)
                   (sort-by sort-by-fn)
                   (partition-all columns))]
      (for [property row]
        (let [stack (stack/create)
              texture-region (get-texture-region (or (:entity/image property)
                                                     (first (:animation/frames (:entity/animation property)))))]
          (run! #(group/add-actor! stack %)
                [(doto (image-button/create
                         (doto (texture-region-drawable/create texture-region)
                           (texture-region-drawable/set-min-size! (* image-scale (texture-region/region-width texture-region))
                                                                  (* image-scale (texture-region/region-height texture-region)))))
                   (actor/add-listener! (change-listener/create
                                    (fn [event actor]
                                      (clicked-id-fn actor (:property/id property)))))
                   (actor/add-listener! (text-tooltip (binding [*print-level* 2]
                                                        (with-out-str
                                                          (pprint/pprint property))))))
                 (doto (new-label (extra-info-text property))
                   (actor/set-touchable! touchable/disabled))])
          stack)))))

(defn property-overview-window [property-type clicked-id-fn {:keys [editor/new-window
                                                                    editor/new-text-button] :as editor}]
  (let [window (new-window "Edit")]
    (doseq [row (property-overview-rows property-type clicked-id-fn editor)]
      (doseq [actor row]
        (table/add! window actor))
      (table/row! window))
    (layout/pack! window)
    (table/add! (window/title-table window)
          (doto (new-text-button "X")
            (actor/add-listener! (change-listener/create
                             (fn [_event _actor]
                               (actor/remove! window))))))
    (window/set-modal! window true)
    window))

(defn scaled-image-button [texture-region scale]
  (image-button/create
   (doto (texture-region-drawable/create texture-region)
     (texture-region-drawable/set-min-size! (* scale (texture-region/region-width texture-region))
                                            (* scale (texture-region/region-height texture-region))))))

(defn create-image-widget [image get-texture-region]
  (scaled-image-button (get-texture-region image) 2))

(defn create-animation-widget 
  [{:keys [pad columns]}]
  (let [table (table/create)]
    (cell/pad! (table/defaults table) pad)
    (doseq [actor columns]
      (table/add! table actor))
    (table/row! table)
    (layout/pack! table)
    table))

(defn- boolean-widget-value [widget]
  (check-box/checked? widget))

(defn- create-boolean-widget [checked? skin]
  (doto (check-box/create "" skin)
    (check-box/set-checked! checked?)))

(defn- default-widget-value [widget]
  ((actor/user-object widget) 1))

(defn- truncate ^String [s limit]
  (if (> (count s) limit)
    (str (subs s 0 limit) "...")
    s))

(defn- create-default-widget [v skin]
  (label/create (truncate (binding [*print-level* nil]
                            (pr-str v))
                          60)
                skin))

(defn- enum-widget-value [widget]
  (edn/read-string (select-box/selected widget)))

(defn- create-enum-widget [schema v skin]
  (doto (select-box/create skin)
    (select-box/set-items! (into-array (map pr-str (rest schema))))
    (select-box/set-selected! (pr-str v))))

(defn- number-widget-value [widget]
  (edn/read-string (text-field/text widget)))

(defn- create-number-widget [schema v skin]
  (doto (text-field/create (pr-str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))

(defn- string-widget-value [widget]
  (text-field/text widget))

(defn- create-string-widget [schema v skin]
  (doto (text-field/create (str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))

(defn- val-max-widget-value [widget]
  (edn/read-string (text-field/text widget)))

(defn- create-val-max-widget [schema v skin]
  (doto (text-field/create (pr-str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))

(defn- list-sounds-table [pad rows]
  (let [table (table/create)]
    (cell/pad! (table/defaults table) pad)
    (doseq [columns rows]
      (doseq [actor columns]
        (table/add! table actor))
      (table/row! table))
    (layout/pack! table)
    table))

(defn- create-sound-widget [sound-names sound-name play-sound! skin stage-viewport stage-add-actor!]
  (let [table (table/create)]
    (cell/pad! (table/defaults table) 5)
    (letfn [(play-sound-button [sound-name]
              (doto (text-button/create "play!" skin)
                (actor/add-listener! (change-listener/create
                                 (fn [event _actor]
                                   (play-sound! sound-name))))))
            (choose-sound-button [sound-name]
              (doto (text-button/create sound-name skin)
                (actor/add-listener! (change-listener/create
                                 (fn [event actor]
                                   (group/clear-children! table)
                                   (doseq [cell (sound-columns-fn sound-name)]
                                     (table/add! table (:actor cell)))
                                   (table/row! table)
                                   (actor/remove! (actor/first-ascendant actor window/class))
                                   (layout/pack! (actor/first-ascendant table window/class))
                                   (let [[k _] (actor/user-object table)]
                                     (actor/set-user-object! table [k sound-name])))))))
            (choose-sound-window []
              (let [list-table (list-sounds-table
                                 5
                                 (for [sound-name sound-names]
                                   [(choose-sound-button sound-name)
                                    (play-sound-button sound-name)]))
                    window (window/create "Choose" skin)]
                (doto (table/add! window (scroll-pane/create list-table skin))
                  (cell/width! (+ (actor/width list-table) 50))
                  (cell/height! (min (- (viewport/world-height stage-viewport) 50)
                                (actor/height list-table))))
                (table/row! window)
                (layout/pack! window)
                (table/add! (window/title-table window)
                      (doto (text-button/create "X" skin)
                        (actor/add-listener! (change-listener/create
                                         (fn [_event _actor]
                                           (actor/remove! window))))))
                (window/set-modal! window true)
                window))
            (sound-columns-fn [sound-name]
              [{:actor (doto (text-button/create sound-name skin)
                         (actor/add-listener! (change-listener/create
                                          (fn [_event _actor]
                                            (open-select-fn)))))}
               {:actor (play-sound-button sound-name)}])
            (open-select-fn []
              (stage-add-actor! (choose-sound-window)))]
      (doseq [cell (if sound-name
                     (sound-columns-fn sound-name)
                     [{:actor
                       (doto (text-button/create "No sound" skin)
                         (actor/add-listener! (change-listener/create
                                          (fn [_event _actor]
                                            (open-select-fn)))))}])]
        (table/add! table (:actor cell)))
      (table/row! table)
      table)))

(defn- one-to-one-widget-value [widget]
  (->> (group/children widget)
       (keep actor/user-object)
       first))

(defn- create-one-to-one-widget [[_ property-type] property-id editor]
  (let [table (table/create)]
    (cell/pad! (table/defaults table) 5)
    (letfn [(add-one-to-one-rows [property-id]
              (let [redo-rows (fn [id]
                                (group/clear-children! table)
                                (add-one-to-one-rows id)
                                (layout/pack! (actor/first-ascendant table window/class)))]
                (doseq [row (cond-> []
                              (not property-id) ; can even be nil?
                              (conj [{:actor (doto ((:editor/new-text-button editor) "+")
                                               (actor/add-listener! (change-listener/create
                                                                (fn [event _actor]
                                                                  ((:editor/add-actor! editor)
                                                                   (property-overview-window
                                                                     property-type
                                                                     (fn [actor id]
                                                                       (actor/remove! (actor/first-ascendant actor window/class))
                                                                       (redo-rows id))
                                                                     editor))))))}])
                              property-id
                              ; duplicated property-display-widget for a property ...
                              ; with one-to-many
                              (conj [{:actor (let [property ((:editor/get-property editor) property-id)]
                                               (doto (image/create-from-texture-region ((:editor/get-texture-region editor) 
                                                                            (or (:entity/image property)
                                                                                (first (:animation/frames (:entity/animation property))))))
                                                 (actor/add-listener! ((:editor/text-tooltip editor) 
                                                                 (binding [*print-level* 2]
                                                                   (with-out-str
                                                                     (pprint/pprint property)))))
                                                 (actor/set-user-object! property-id)))}]
                                    [{:actor (doto ((:editor/new-text-button editor) "-")
                                               (actor/add-listener! (change-listener/create
                                                                (fn [event _actor]
                                                                  (redo-rows nil)))))}]))]
                  (doseq [cell row]
                    ; actor not necessary
                    (table/add! table (:actor cell)))
                  (table/row! table))))]
      (add-one-to-one-rows property-id))
    table))

(defn- one-to-many-widget-value [widget]
  (->> (group/children widget)
       (keep actor/user-object)
       set))

(defn- create-one-to-many-widget [[_ property-type] property-ids skin editor]
  (let [table (table/create)]
    (cell/pad! (table/defaults table) 5)
    (letfn [(add-one-to-many-rows [property-ids]
              (let [redo-rows (fn [property-ids]
                                (group/clear-children! table)
                                (add-one-to-many-rows property-ids)
                                ; editor window callback instead ? 
                                (layout/pack! (actor/first-ascendant table window/class))
                                )]
                (doseq [row
                        [[{:actor (doto (text-button/create "+" skin)
                                    (actor/add-listener! (change-listener/create
                                                     (fn [event _actor]
                                                       ((:editor/add-actor! editor)
                                                        (property-overview-window
                                                          property-type
                                                          (fn [actor id]
                                                            (actor/remove! (actor/first-ascendant actor window/class))
                                                            (redo-rows (conj property-ids id)))
                                                          editor))))))}]
                         (for [property-id property-ids]
                           (let [property ((:editor/get-property editor) property-id)]
                             {:actor (doto (image/create-from-texture-region ((:editor/get-texture-region editor) 
                                                                  (or (:entity/image property)
                                                                      (first (:animation/frames (:entity/animation property))))))
                                       (actor/add-listener! (text-tooltip/create (binding [*print-level* 2]
                                                                      (with-out-str
                                                                        (pprint/pprint property)))
                                                                    skin))
                                       (actor/set-user-object! property-id))}))
                         (for [id property-ids]
                           {:actor (doto (text-button/create "-" skin)
                                     (actor/add-listener! (change-listener/create
                                                      (fn [event _actor]
                                                        (redo-rows (disj property-ids id))))))})]]
                  (doseq [cell row]
                    (table/add! table (:actor cell)))
                  (table/row! table))))]
      (add-one-to-many-rows property-ids))
    table))

(defn- k-label-text [k]
  (name k))

(defn- add-horiz-sep! [table colspan]
  (doto (table/add! table nil)
    (cell/pad-top! 2)
    (cell/pad-bottom! 2)
    (cell/colspan! colspan)
    (cell/fill-x!)
    (cell/expand-x!))
  (table/row! table))

(defn- add-component-window 
  [skin columns]
  (let [window (window/create "Choose" skin)]
    (cell/pad! (table/defaults window) 5)
    (table/add! (window/title-table window)
          (doto (text-button/create "X" skin)
            (actor/add-listener! (change-listener/create
                             (fn [_event _actor]
                               (actor/remove! window))))))
    (window/set-modal! window true)
    (doseq [{:keys [text f]} columns]
      (table/add! window (doto (text-button/create text skin)
                     (actor/add-listener! (change-listener/create
                                      (fn [event _actor]
                                        (actor/remove! window)
                                        (f))))))
      (table/row! window))
    (layout/pack! window)
    window))

(defn- map-widget-table-create
  [{:keys [map-schema-keys
           rebuild-editor-window!
           schema
           k->widget
           k->optional?
           ks-sorted
           opt?
           widget-value
           skin
           editor
           new-default-value-widget
           map-schema-keys
           ]}]
  (let [table (doto (table/create)
                (actor/set-name! "moon.db.schema.map.ui.widget"))
        component-row-table (fn
                              [{:keys [display-remove-component-button?
                                       k]}]
                              (let [row-table (table/create)]
                                (cell/pad! (table/defaults table) 2)
                                (doto (table/add! row-table (when display-remove-component-button?
                                                        (doto (text-button/create "-" skin)
                                                          (actor/add-listener! (change-listener/create
                                                                           (fn [event _actor]
                                                                             (actor/remove! (first (filter (fn [actor]
                                                                                                       (and (actor/user-object actor)
                                                                                                            (= k ((actor/user-object actor) 0))))
                                                                                                     (group/children table))))
                                                                             (rebuild-editor-window!)))))
                                                        (label/create "." skin)))
                                  (cell/left!))
                                (table/add! row-table (label/create (k-label-text k) skin))
                                (table/row! row-table)
                                (layout/pack! row-table)
                                row-table))
        add-component-row! (fn [opts]
                             (doto (table/add! table (component-row-table opts))
                               (cell/right!))
                             (doto (table/add! table nil)
                               (cell/pad-top! 2)
                               (cell/pad-bottom! 2)
                               (cell/fill-y!)
                               (cell/expand-y!))
                             (doto (table/add! table (:editor-widget opts))
                               (cell/left!))
                             (table/row! table))
        open-add-component-window! (fn []
                                     ((:editor/add-actor! editor)
                                      (let [columns (sort (remove (set (keys (widget-value schema table)))
                                                                  map-schema-keys))
                                            columns (for [k columns]
                                                      {:text (name k)
                                                       :f (fn []
                                                            (add-component-row!
                                                              {:editor-widget (new-default-value-widget k)
                                                               :k k
                                                               :display-remove-component-button? (k->optional? k)})
                                                            (rebuild-editor-window!))})]
                                        (add-component-window skin columns))))
        colspan 3]
    (cell/pad! (table/defaults table) 5)
    (when opt?
      (doto (table/add! table (doto (text-button/create "Add component" skin)
                          (actor/add-listener! (change-listener/create
                                           (fn [_event _actor]
                                             (open-add-component-window!))))))
        (cell/colspan! colspan))
      (table/row! table)
      (add-horiz-sep! table colspan))
    (doseq [[i k] (map-indexed vector ks-sorted)]
      (when (pos? i)
        (add-horiz-sep! table colspan))
      (add-component-row! {:editor-widget (k->widget k)
                           :k k
                           :display-remove-component-button? (k->optional? k)}))
    table))

(defn play-sound!* [sounds sound-name]
  (sound/play! (safe-get sounds sound-name)))

(defn- property-editor-table [widget on-save on-delete skin]
  (let [table (table/create)]
    (cell/pad! (table/defaults table) 5)
    (doto (table/add! table widget)
      (cell/colspan! 2))
    (table/row! table)
    (doto (table/add! table (doto (text-button/create "Save [LIGHT_GRAY](ENTER)[]" skin)
                        (actor/add-listener! (change-listener/create
                                         (fn [event actor]
                                           (on-save actor))))))
      (cell/center!))
    (doto (table/add! table (doto (text-button/create "Delete" skin)
                        (actor/add-listener! (change-listener/create
                                         (fn [event actor]
                                           (on-delete actor))))))
      (cell/center!))
    (table/row! table)
    (layout/pack! table)
    table))

(defn editor-application-listener 
  [editor-config]
  (let [properties-file (io/resource "properties.edn")
        schemas (-> "schema.edn" io/resource slurp edn/read-string)
        db (atom (create-db schemas (-> properties-file slurp edn/read-string)))
        editor-stage (atom nil)
        property-type->overview-table-props {:properties/audiovisuals {:columns 10
                                                                       :image-scale 2
                                                                       :sort-by-fn (comp name :property/id)
                                                                       :extra-info-text (constantly "")}
                                             :properties/creatures    {:columns 15
                                                                       :image-scale 1.5
                                                                       :sort-by-fn #(vector (:creature/level %)
                                                                                            (name (:entity/species %))
                                                                                            (name (:property/id %)))
                                                                       :extra-info-text #(str (:creature/level %))}
                                             :properties/items        {:columns 20
                                                                       :image-scale 1.1
                                                                       :sort-by-fn #(vector (name (:item/slot %))
                                                                                            (name (:property/id %)))
                                                                       :extra-info-text (constantly "")}
                                             :properties/projectiles  {:columns 16
                                                                       :image-scale 2
                                                                       :sort-by-fn (comp name :property/id)
                                                                       :extra-info-text (constantly "")}
                                             :properties/skills       {:columns 16
                                                                       :image-scale 2
                                                                       :sort-by-fn (comp name :property/id)
                                                                       :extra-info-text (constantly "")}}
        batch (atom nil)
        textures (atom nil)
        image->texture-region #(get-texture-region @textures %)
        skin (atom nil)
        sounds (atom nil)
        editor {:editor/all-raw
                (fn [property-type]
                  (all-raw @db property-type))
                :editor/get-property
                (fn [property-id]
                  (safe-get @db property-id))
                :editor/get-texture-region
                image->texture-region
                :editor/property-type->overview-table-props
                property-type->overview-table-props
                :editor/text-tooltip
                (fn [text]
                  (text-tooltip/create text @skin))
                :editor/new-label
                (fn [text]
                  (label/create text @skin))
                :editor/new-window
                (fn [title]
                  (window/create title @skin))
                :editor/new-text-button
                (fn [title]
                  (text-button/create title @skin))
                :editor/get-viewport
                #(stage/viewport @editor-stage)
                :editor/add-actor!
                #(stage/add-actor! @editor-stage %)
                :editor/sound-names
                (fn [] (keys @sounds))
                :editor/play-sound!
                (fn [sound-name] (play-sound!* @sounds sound-name))
                }
        property-k-order-index (zipmap [:property/id
                                        :property/pretty-name
                                        :entity/image
                                        :entity/animation
                                        :entity/species
                                        :creature/level
                                        :entity/width
                                        :entity/height
                                        :entity/flying?
                                        :item/slot
                                        :projectile/speed
                                        :projectile/max-range
                                        :projectile/piercing?
                                        :skill/action-time-modifier-key
                                        :skill/action-time
                                        :skill/start-action-sound
                                        :skill/cost]
                                       (range))
        property-types [:properties/audiovisuals
                        :properties/creatures 
                        :properties/items
                        :properties/projectiles
                        :properties/skills]
        property-editor-window (fn property-editor-window [property]
                                 (let [schema (get schemas (keyword "properties" (namespace (:property/id property))))
                                       widget-value (fn widget-value [[schema-k] widget]
                                                      (case schema-k
                                                        :s/animation (default-widget-value widget)
                                                        :s/boolean (boolean-widget-value widget)
                                                        :s/enum (enum-widget-value widget)
                                                        :s/image (default-widget-value widget)
                                                        :s/map (into {}
                                                                     (for [widget (filter (comp vector? actor/user-object) (group/children widget))
                                                                           :let [[k _] (actor/user-object widget)]]
                                                                       [k (widget-value (get schemas k) widget)]))
                                                        :s/number (number-widget-value widget)
                                                        :s/one-to-many (one-to-many-widget-value widget)
                                                        :s/one-to-one (one-to-one-widget-value widget)
                                                        :s/qualified-keyword (default-widget-value widget) 
                                                        :s/some (default-widget-value widget)
                                                        :s/sound (default-widget-value widget)
                                                        :s/string (string-widget-value widget)
                                                        :s/val-max (val-max-widget-value widget)))
                                       rebuild-editor-window! (fn []
                                                                (let [window (-> (stage/root @editor-stage)
                                                                                 (group/find-actor "moon.ui.clojure.editor-window"))
                                                                      map-widget-table (group/find-actor window "moon.db.schema.map.ui.widget")
                                                                      property (widget-value schema map-widget-table)]
                                                                  (actor/remove! window)
                                                                  (stage/add-actor! @editor-stage (property-editor-window property))))
                                       create-map-widget (fn [schema m build-widget]
                                                           (map-widget-table-create
                                                             {:map-schema-keys (map-keys (malli-form schemas schema))
                                                              :rebuild-editor-window! rebuild-editor-window!
                                                              :schema schema
                                                              :widget-value widget-value
                                                              :skin @skin
                                                              :editor editor
                                                              :new-default-value-widget (fn [k]
                                                                                          (build-widget (get schemas k)
                                                                                                        k
                                                                                                        (let [schema (get schemas k)]
                                                                                                          (cond
                                                                                                            (#{:s/map} (schema 0)) {}
                                                                                                            :else nil))))
                                                              :k->widget (into {}
                                                                               (for [[k v] m]
                                                                                 [k (build-widget (get schemas k) k v)]))
                                                              :k->optional? #(optional? (malli-form schemas schema) %)
                                                              :ks-sorted (map key (sort-by (fn [[k _]] (get property-k-order-index k Integer/MAX_VALUE)) m))
                                                              :opt? (seq (set/difference (optional-keyset (malli-form schemas schema))
                                                                                         (set (keys m))))}))
                                       create-widget (fn create-widget [[schema-k :as schema] v]
                                                       (case schema-k
                                                         :s/animation (create-animation-widget 
                                                                        {:columns (for [image (:animation/frames v)]
                                                                                    (scaled-image-button (image->texture-region image) 2))
                                                                         :pad 1})
                                                         :s/boolean (create-boolean-widget v @skin)
                                                         :s/enum (create-enum-widget schema v @skin)
                                                         :s/image (create-image-widget v image->texture-region)
                                                         :s/map (create-map-widget schema v
                                                                                   (fn build-widget [schema k v] 
                                                                                     (let [widget (create-widget schema v)]
                                                                                       (actor/set-user-object! widget [k v])
                                                                                       widget)))
                                                         :s/number (create-number-widget schema v @skin)
                                                         :s/one-to-many (create-one-to-many-widget schema v @skin editor)
                                                         :s/one-to-one (create-one-to-one-widget schema v editor)
                                                         :s/qualified-keyword (create-default-widget v @skin)
                                                         :s/some (create-default-widget v @skin)
                                                         :s/sound (create-sound-widget ((:editor/sound-names editor))
                                                                                       v 
                                                                                       (:editor/play-sound! editor)
                                                                                       @skin
                                                                                       ((:editor/get-viewport editor))
                                                                                       (:editor/add-actor! editor))
                                                         :s/string (create-string-widget schema v @skin)
                                                         :s/val-max (create-val-max-widget schema v @skin)))
                                       widget (create-widget schema property)
                                       scroll-pane-height (viewport/world-height (stage/viewport @editor-stage))
                                       property-id (:property/id property)
                                       with-window-close (fn [f]
                                                           (fn [actor]
                                                             (try
                                                               (swap! db f)
                                                               (actor/remove! (actor/first-ascendant actor window/class))
                                                               (catch Throwable t
                                                                 (binding [*print-level* 3]
                                                                   (pretty-repl/pretty-pst t 24))))))
                                       on-delete (with-window-close 
                                                   (fn [db]
                                                     (assert (contains? db property-id))
                                                     (let [new-db (dissoc db property-id)]
                                                       (save! new-db properties-file)
                                                       new-db)))
                                       on-save (with-window-close 
                                                 (fn [db]
                                                   (let [property (widget-value schema widget)
                                                         id (:property/id property)]
                                                     (assert (contains? property :property/id))
                                                     (assert (contains? db id))
                                                     (let [k (keyword "properties" (namespace (:property/id property)))]
                                                       (validate-hum (malli-schema schemas k) property))
                                                     (let [new-db (assoc db id property)]
                                                       (save! new-db properties-file)
                                                       new-db))))
                                       table (property-editor-table widget on-save on-delete @skin)
                                       window (window/create "[SKY]Property[]" @skin)]
                                   (cell/pad! (table/defaults window) 5)
                                   (let [c (table/add! window (scroll-pane/create table @skin))]
                                     (cell/width! c (+ (actor/width table) 50))
                                     (cell/height! c (min (- scroll-pane-height 50)
                                                     (actor/height table))))
                                   (table/row! window)
                                   (layout/pack! window)
                                   (table/add! (window/title-table window)
                                         (doto (text-button/create "X" @skin)
                                           (actor/add-listener! (change-listener/create
                                                            (fn [_event _actor]
                                                              (actor/remove! window))))))
                                   (window/set-modal! window true)
                                   (group/add-actor! window (actor/create
                                                              {:act!
                                                               (fn [this _delta]
                                                                 (when (input/key-just-pressed? (gdx/input) :keys/enter)
                                                                   (on-save this)))
                                                               :draw!
                                                               (fn [_this _batch _parent-alpha])}))
                                   (actor/set-name! window "moon.ui.clojure.editor-window")
                                   window))
        main-window (fn []
                      (let [window (window/create "Edit" @skin)]
                        (doseq [property-type property-types]
                          (table/add! window (doto (text-button/create (str/capitalize (name property-type)) @skin)
                                         (actor/add-listener! (change-listener/create
                                                          (fn [_event _actor]
                                                            (stage/add-actor! @editor-stage
                                                                        (property-overview-window
                                                                          property-type
                                                                          (fn [_actor id]
                                                                            (stage/add-actor! @editor-stage
                                                                                        (property-editor-window ((:editor/get-property editor) id))))
                                                                          editor)))))))
                          (table/row! window))
                        (layout/pack! window)
                        window))]
    {:create!
     (fn [{:keys [new-sound internal set-input-processor!] :as capabilities}]
       (reset! batch (sprite-batch/create))
       (reset! skin (skin/create (internal (:skin editor-config))))
       (bitmap-font-data/set-markup-enabled!
        (bitmap-font/data (skin/font @skin "default-font"))
        true)
       (reset! editor-stage (stage/create (fit-viewport/create 1440 900) @batch))
       (reset! sounds (into {}
                            (for [path (-> (:sounds editor-config) io/resource slurp edn/read-string)]
                              [path (new-sound (internal path))])))
       (reset! textures (create-textures internal (:textures editor-config)))
       (set-input-processor! @editor-stage)
       (stage/add-actor! @editor-stage (main-window)))
     :dispose!
     (fn []
       (run! disposable/dispose! (vals @sounds))
       (disposable/dispose! @batch)
       (disposable/dispose! @skin)
       (run! disposable/dispose! (vals @textures)))
     :render!
     (fn [{:keys [clear-screen!]}]
       (clear-screen! 0 0 0 0)
       (stage/act! @editor-stage)
       (stage/draw! @editor-stage))
     :resize!
     (fn [width height]
       (viewport/update! (stage/viewport @editor-stage) width height true))
     }))

(defn create-capabilities []
  (let [audio (gdx/audio)
        files (gdx/files)
        graphics (gdx/graphics)
        input (gdx/input)]
    {
     :frames-per-second (fn [] 
                          (graphics/frames-per-second graphics))
     :internal (fn [path] 
                 (files/internal files path))
     :new-sound (fn [file-handle]
                  (audio/new-sound audio file-handle))
     :new-cursor (fn [pixmap hotspot-x hotspot-y]
                   (graphics/new-cursor graphics pixmap hotspot-x hotspot-y))
     :set-input-processor! (fn [processor]
                             (input/set-processor! input processor))
     :clear-screen! (fn [r g b a]
                      (screen-utils/clear r g b a))
     :get-input-position (fn []
                           [(input/x input)
                            (input/y input)])
     :set-cursor! (fn [cursor]
                    (graphics/set-cursor! graphics cursor))
     :get-delta-time #(graphics/delta-time graphics)
     :key-pressed? #(input/key-pressed? input %)
     :key-just-pressed? #(input/key-just-pressed? input %)
     :button-just-pressed? #(input/button-just-pressed? input %)
     }))

(defn lwjgl3-application! 
  [{:keys [create! 
           dispose! 
           render! 
           resize!]}
   {:keys [title 
           width 
           height 
           fps]}]
  (lwjgl3-application/create
    (application-listener/create
      {:create! (fn [] (create! (create-capabilities)))
       :dispose! dispose!
       :render! (fn [] (render! (create-capabilities)))
       :resize! resize!
       :pause! (fn [])
       :resume! (fn [])})
    (doto (lwjgl3-application-configuration/create)
      (lwjgl3-application-configuration/set-title! title)
      (lwjgl3-application-configuration/set-windowed-mode! width height)
      (lwjgl3-application-configuration/set-foreground-fps! fps))))

(defn draw-text-demo-listener []
  (let [batch (atom nil)
        font (atom nil)
        viewport (atom nil)
        shape-drawer (atom nil)
        shape-drawer-texture (atom nil)
        unit-scale (atom 1.0)
        world-unit-scale (float (/ 48))
        samples [{:title "basic / up?"
                  :x 220 :y 720
                  :opts {:text "Hello draw-text!"
                         :scale 1
                         :up? true}}
                 {:title "scale 2.5 / up?"
                  :x 720 :y 720
                  :opts {:text "Player message"
                         :scale 2.5
                         :up? true}}
                 {:title "multiline / up?"
                  :x 220 :y 480
                  :opts {:text "line one\nline two\nline three"
                         :scale 1
                         :up? true}}
                 {:title "multiline / down"
                  :x 720 :y 480
                  :opts {:text "line one\nline two\nline three"
                         :scale 1
                         :up? false}}
                 {:title "markup colors"
                  :x 220 :y 240
                  :opts {:text "[RED]red[] [GREEN]green[] [GOLD]gold[]"
                         :scale 1.25
                         :up? true}}
                 {:title "string-effect style"
                  :x 720 :y 240
                  :opts {:text "+12"
                         :scale 2
                         :up? true}}]]
    {:create!
     (fn [{:keys [internal]}]
       (reset! batch (sprite-batch/create))
       (reset! font (create-default-font! internal
                                          {:path "fonts/films.EXL_____.ttf"
                                           :size 16
                                           :quality-scaling 2
                                           :use-integer-positions? false}))
       (reset! shape-drawer-texture (white-pixel-texture))
       (reset! shape-drawer (shape-drawer/create @batch (texture-region/create @shape-drawer-texture 1 0 1 1)))
       (reset! viewport (fit-viewport/create 1440
                                             900
                                             (doto (orthographic-camera/create)
                                               (orthographic-camera/set-to-ortho! false 1440 900)))))

     :dispose!
     (fn []
       (disposable/dispose! @batch)
       (disposable/dispose! @font)
       (disposable/dispose! @shape-drawer-texture))

     :render!
     (fn [{:keys [clear-screen! key-just-pressed?]}]
       (when (key-just-pressed? :keys/u)
         (swap! unit-scale #(if (== ^double % 1.0)
                              world-unit-scale
                              1.0)))
       (clear-screen! 0.08 0.08 0.1 1)
       (batch/set-projection-matrix! @batch (orthographic-camera/combined (viewport/camera @viewport)))
       (batch/begin @batch)
       (let [b @batch
             f @font
             us @unit-scale
             sd @shape-drawer
             marker-color (color/float-bits 1 0.3 0.3 1)
             guide-color (color/float-bits 0.35 0.35 0.45 1)]
         (doseq [{:keys [title x y opts]} samples]
           (line! sd [(- x 20) y] [(+ x 20) y] guide-color)
           (line! sd [x (- y 20)] [x (+ y 20)] guide-color)
           (filled-circle! sd [x y] 3 marker-color)
           (draw-text! b f us (assoc opts :x x :y y))
           (draw-text! b f us {:text title
                               :x x
                               :y (- y 40)
                               :scale 0.75
                               :up? false}))
         (draw-text! b f 1 {:text (str "draw-text! demo\n"
                                       "red cross = x/y anchor\n"
                                       "unit-scale: " us "   [U] toggle 1 <-> world (" world-unit-scale ")")
                            :x 720
                            :y 880
                            :scale 1.1
                            :up? true}))
       (batch/end @batch))

     :resize!
     (fn [width height]
       (viewport/update! @viewport width height true))}))

(defn draw-text-demo! []
  (lwjgl3-application-configuration/use-glfw-async!)
  (lwjgl3-application! (draw-text-demo-listener)
                       {:title "draw-text! demo"
                        :width 1440
                        :height 900
                        :fps 60}))

(defn levelgen! []
  (lwjgl3-application-configuration/use-glfw-async!)
  (lwjgl3-application! (levelgen-listener)
                       {:title "Levelgen", 
                        :width 1440, 
                        :height 900, 
                        :fps 60}))

(defn editor! []
  (lwjgl3-application-configuration/use-glfw-async!)
  (lwjgl3-application! (editor-application-listener
                         {
                          :skin "skin/uiskin.json"
                          :sounds "sounds.edn"
                          :textures {:folder "resources/"
                                     :extensions #{"png" "bmp"}}
                          })
                       {:title "Editor"
                        :width 1440 
                        :height 900 
                        :fps 60}))

(defn game! []
  (let [game-schemas (-> "schema.edn" io/resource slurp edn/read-string)
        game-db (create-db game-schemas (-> "properties.edn" io/resource slurp edn/read-string))
        k->info-fn {:creature/level (fn [v]
                                      (str "Level: " v))
                    :entity/stats (fn [entity-stats]
                                    (stats-format-text entity-stats))
                    :effects.target/convert (fn [_]
                                              "Converts target to your side.")
                    :effects.target/damage (fn [{[min max] :damage/min-max}]
                                             (str min "-" max " damage"))
                    :effects.target/kill (fn [_]
                                           "Kills target")
                    :effects.target/melee-damage (fn [_]
                                                   "Damage based on entity strength.")
                    :effects.target/spiderweb (fn [_]
                                                "Spiderweb slows 50% for 5 seconds.")
                    :effects.target/stun (fn [duration]
                                           (str "Stuns for " (readable duration) " seconds"))
                    :effects/spawn (fn [{:keys [property/pretty-name]}]
                                     (str "Spawns a " pretty-name))
                    :effects/target-all (fn [_]
                                          "All visible targets")
                    :entity/faction (fn [faction]
                                      (str "Faction: " (name faction)))
                    :entity/fsm (fn [fsm]
                                  (str "State: " (name (:state fsm))))
                    :stats/modifiers (fn [mods]
                                       (mods-format-text mods))
                    :entity/skills (fn [skills]
                                     (when (seq skills)
                                       (str "Skills: " (str/join "," (map name (keys skills))))))
                    :entity/species (fn [species]
                                      (str "Creature - " (str/capitalize (name species))))
                    :projectile/piercing? (fn [_]
                                            "Piercing")
                    :property/pretty-name (fn [v]
                                            v)
                    :skill/action-time (fn [v]
                                         (str "Action-Time: " (readable v) " seconds"))
                    :skill/action-time-modifier-key (fn [v]
                                                      (case v
                                                        :stats/cast-speed "Spell"
                                                        :stats/attack-speed "Attack"))
                    :skill/cooldown (fn [v]
                                      (str "Cooldown: " (readable v) " seconds"))
                    :skill/cost (fn [v]
                                  (str "Cost: " v " Mana"))
                    :maxrange (fn [v]
                                (str "Range: " v " Meters."))}
        k-order-index (zipmap [:property/pretty-name
                               :skill/action-time-modifier-key
                               :skill/action-time
                               :skill/cooldown
                               :skill/cost
                               :skill/effects
                               :entity/species
                               :creature/level
                               :entity/stats
                               :entity/delete-after-duration
                               :projectile/piercing?
                               :entity/projectile-collision
                               :maxrange
                               :entity-effects]
                              (range))
        k->colors {:property/pretty-name "PRETTY_NAME"
                   :stats/modifiers "CYAN"
                   :maxrange "LIGHT_GRAY"
                   :creature/level "GRAY"
                   :projectile/piercing? "LIME"
                   :skill/action-time-modifier-key "VIOLET"
                   :skill/action-time "GOLD"
                   :skill/cooldown "SKY"
                   :skill/cost "CYAN"
                   :entity/delete-after-duration "LIGHT_GRAY"
                   :entity/faction "SLATE"
                   :entity/fsm "YELLOW"
                   :entity/species "LIGHT_GRAY"
                   :entity/temp-modifier "LIGHT_GRAY"}
        info-text (fn info-text [entity elapsed-time]
                    (let [component-info (fn [[k v]]
                                           (let [s (case k
                                                     :entity/delete-after-duration
                                                     (str "Remaining: " (readable (timer-ratio v elapsed-time)) "/1")

                                                     :entity/temp-modifier
                                                     (str "Spiderweb - remaining: " (readable (timer-ratio (:counter v) elapsed-time)) "/1")

                                                     :skill/cooling-down?
                                                     (str "Cooldown: " (readable (timer-ratio v elapsed-time)) "/1")

                                                     (when-let [info-fn (k->info-fn k)]
                                                       (str (info-fn v))))]
                                             (if-let [color (k->colors k)]
                                               (str "[" color "]" s "[]")
                                               s)))]
                      (->> entity
                           (sort-by (fn [[k _]] (get k-order-index k Integer/MAX_VALUE)))
                           (keep (fn [{k 0 v 1 :as component}]
                                   (str (try (component-info component)
                                             (catch Throwable _t
                                               (str "*info-error* " k)))
                                        (when (map? v)
                                          (str "\n" (info-text v elapsed-time))))))
                           (str/join "\n")
                           remove-newlines)))
        slot->y-sprite-idx #:inventory.slot {:weapon 0
                                             :shield 1
                                             :rings 2
                                             :necklace 3
                                             :helm 4
                                             :cloak 5
                                             :chest 6
                                             :leg 7
                                             :glove 8
                                             :boot 9
                                             :bag 10}
        build-values (fn build-values [property]
                       (reduce (fn [m k]
                                 (assoc m k
                                        (try (let [v (k m)]
                                               (case (first (get game-schemas k))
                                                 :s/map
                                                 (build-values v)

                                                 :s/one-to-many
                                                 (set (map (fn [property-id]
                                                             (build-values (safe-get game-db property-id)))
                                                           v))

                                                 :s/one-to-one
                                                 (build-values (safe-get game-db v))

                                                 v))
                                             (catch Throwable t
                                               (throw (ex-info " " {:k k
                                                                    :v (k m)} t))))))
                               property
                               (keys property)))
        z-orders [:z-order/on-ground
                  :z-order/ground
                  :z-order/flying
                  :z-order/effect]
        minimum-size 0.39
        max-delta 0.04
        max-speed (/ minimum-size max-delta)
        fsms {:npc (fsm/fsm-inc
                     [[:npc-sleeping
                       :kill -> :npc-dead
                       :stun -> :stunned
                       :alert -> :npc-idle]
                      [:npc-idle
                       :kill -> :npc-dead
                       :stun -> :stunned
                       :start-action -> :active-skill
                       :movement-direction -> :npc-moving]
                      [:npc-moving
                       :kill -> :npc-dead
                       :stun -> :stunned
                       :timer-finished -> :npc-idle]
                      [:active-skill
                       :kill -> :npc-dead
                       :stun -> :stunned
                       :action-done -> :npc-idle]
                      [:stunned
                       :kill -> :npc-dead
                       :effect-wears-off -> :npc-idle]
                      [:npc-dead]])
              :player (fsm/fsm-inc
                        [[:player-idle
                          :kill -> :player-dead
                          :stun -> :stunned
                          :start-action -> :active-skill
                          :movement-input -> :player-moving]
                         [:player-moving
                          :kill -> :player-dead
                          :stun -> :stunned
                          :no-movement-input -> :player-idle]
                         [:active-skill
                          :kill -> :player-dead
                          :stun -> :stunned
                          :action-done -> :player-idle]
                         [:stunned
                          :kill -> :player-dead
                          :effect-wears-off -> :player-idle]
                         [:player-dead]])}
        inventory-cell-size 48
        slot->texture-region (fn [slot]
                               (let [width 48
                                     height 48
                                     sprite-x 21
                                     sprite-y (+ (slot->y-sprite-idx slot) 2)
                                     bounds [(* sprite-x width)
                                             (* sprite-y height)
                                             width
                                             height]]
                                 {:image/file "images/items.png"
                                  :image/bounds bounds}))
        factions-iterations {:good 15 :evil 5}
        construct-entity (fn [entity create-timer*]
                           (construct-entity entity create-timer* minimum-size z-orders))
        level-fn create-vampire-level
                    ;create-uf-caves
                    ;create-modules-level
        create-level! (fn [textures image->texture-region]
                        (let [{level-tiled-map :tiled-map
                               level-start :start-position}
                              (level-fn {:level/creature-properties (prepare-creature-tiles
                                                                      (all-raw game-db :properties/creatures)
                                                                      image->texture-region)
                                         :textures textures})]
                          {:tiled-map level-tiled-map
                           :start-position level-start}))
        render-z-order (apply hash-map (interleave z-orders (range)))
        render-layers [#{:entity/mouseover?
                         :stunned}
                       #{:entity/clickable
                         :entity/animation
                         :entity/image
                         :entity/line-render}
                       #{:npc-sleeping
                         :entity/temp-modifier
                         :entity/string-effect}
                       #{:entity/stats
                         :active-skill}]
        font-params {:path "fonts/films.EXL_____.ttf"
                     :size 16
                     :quality-scaling 2
                     :use-integer-positions? false}
        y-mana 80
        rahmenw 150
        rahmenh 26
        y-hp (+ y-mana rahmenh)
        rahmen-file "images/rahmen.png"
        hpcontent-file "images/hp.png"
        manacontent-file "images/mana.png"
        listener (game-listener
                   {
                    :render-z-order render-z-order
                    :render-layers render-layers
                    :font-params font-params
                    :y-mana y-mana
                    :rahmenw rahmenw
                    :rahmenh rahmenh
                    :y-hp y-hp
                    :rahmen-file rahmen-file
                    :hpcontent-file hpcontent-file
                    :manacontent-file manacontent-file
                    :create-level! create-level!
                    :construct-entity construct-entity
                    :inventory-cell-size inventory-cell-size
                    :factions-iterations factions-iterations
                    :slot->texture-region slot->texture-region
                    :fsms fsms
                    :max-speed max-speed
                    :max-delta max-delta
                    :controls-info (str/join "\n"
                                             ["[W][A][S][D] - Move"
                                              "[ESCAPE] - Close windows"
                                              "[I] - Inventory window"
                                              "[E] - Entity Info window"
                                              "[-]/[=] - Zoom"
                                              "[P]/[SPACE] - Unpause"
                                              "Leftmouse click - use skill/drop item on cursor"])
                    :z-orders z-orders
                    :minimum-size minimum-size
                    :game-schemas game-schemas
                    :game-db game-db
                    :build-property (fn [property-id]
                                      (build-values (safe-get game-db property-id)))
                    :info-text info-text
                    :level-fn level-fn
                    :pausing? true
                    :state->pause-game? {:active-skill false
                                         :stunned false
                                         :player-moving false
                                         :player-idle true
                                         :player-dead true
                                         
                                         ; ? if we dont pause anymore and its in idle do we still have to destroy entities outsife of running ?
                                         ; should transcations be self contained ? or systems no intermediary wrong state?
                                         ;:player-item-on-cursor true
                                         }
                    :controls {:zoom-in :keys/minus
                               :zoom-out :keys/equals
                               :unpause-once :keys/p
                               :unpause-continously :keys/space
                               :close-windows-key :keys/escape
                               :toggle-inventory :keys/i
                               :toggle-entity-info :keys/e}
                    :cursors (-> "cursors.edn" io/resource slurp edn/read-string)
                    :sounds (-> "sounds.edn" io/resource slurp edn/read-string)
                    :skin "skin/uiskin.json"
                    :stage {:width 1440 
                            :height 900}
                    :textures {:folder "resources/"
                               :extensions #{"png" "bmp"}}
                    :world-unit-scale (/ 48)
                    :slot->y-sprite-idx slot->y-sprite-idx
                    })
        config {:title "Moon"
                :width 1440 
                :height 900 
                :fps 60}]
    (colors/put "PRETTY_NAME" (color/create 0.84 0.8 0.52 1))
    (lwjgl3-application-configuration/use-glfw-async!)
    (lwjgl3-application! listener config)))
