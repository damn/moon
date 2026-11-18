(ns game)

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
(require '[malli.core :as m])
(def val-max-schema
  (m/schema
   [:and
    [:vector {:min 2 :max 2} [:int {:min 0}]]
    [:fn {:error/fn (fn [{[^int v ^int mx] :value} _]
                      (when (< mx v)
                        (format "Expected max (%d) to be smaller than val (%d)" v mx)))}
     (fn [[^int a ^int b]] (<= a b))]]))

(import '(java.util Random))

(defn create-random []
  (Random.))

(defn next-float [random]
  (Random/.nextFloat random))

(def k->adj-num
  {:wide
   (fn [open-paths random]
     (if (= open-paths 1)
       (case (int (* 3 (next-float random)))
         0 1
         2)
       (case (int (* 4 (next-float random)))
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
       (case (int (* 7 (next-float random)))
         0 0
         1 2
         1)))
   :default
   ; etwas breiter als 1 aber immernoch zu dünn für m ein game -> turn-ratio verringern besser
   (fn [open-paths random]
     (if (= open-paths 1)
       (case (int (* 4 (next-float random)))
         0 1
         1 1
         2 1
         3 2
         1)
       (case (int (* 4 (next-float random)))
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

(require '[clojure.math :as math])
(require '[clojure.string :as str])

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

(defprotocol GetName
  (get-name [_]))

(defprotocol GetWidth
  (get-width [_]))

(defprotocol GetHeight
  (get-height [_]))

(import '(com.badlogic.gdx.maps MapLayer MapLayers MapProperties)
        '(com.badlogic.gdx.maps.tiled TiledMap
                                      TiledMapTile
                                      TiledMapTileLayer
                                      TiledMapTileLayer$Cell
                                      TmxMapLoader)
        '(com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile))

(defn get-keys [map-properties]
  (MapProperties/.getKeys map-properties))

(defn get-values [map-properties]
  (MapProperties/.getValues map-properties))

(defn get-layers [^TiledMap tiled-map]
  (.getLayers tiled-map))

(defprotocol GetProperties
  (get-properties [_]))

(extend-type TiledMap
  GetProperties
  (get-properties [this]
    (.getProperties this)))

(extend-type TiledMapTile
  GetProperties
  (get-properties [this]
    (.getProperties this)))

(extend-type TiledMapTileLayer
  GetProperties
  (get-properties [this]
    (.getProperties this))
  GetWidth
  (get-width [this]
    (.getWidth this))
  GetHeight
  (get-height [this]
    (.getHeight this)))

(defn layers-get [^MapLayers map-layers ^String layer-name]
  (.get map-layers layer-name))

(defn get-cell [^TiledMapTileLayer layer x y]
  (.getCell layer (int x) (int y)))

(defn set-cell! [^TiledMapTileLayer layer x y tile]
  (.setCell layer 
            (int x)
            (int y) 
            (doto (TiledMapTileLayer$Cell.)
              (.setTile ^TiledMapTile tile))))

(defn get-tile [^TiledMapTileLayer$Cell cell]
  (.getTile cell))

(import '(com.badlogic.gdx.graphics.g2d Batch
                                        BitmapFont
                                        BitmapFont$BitmapFontData
                                        SpriteBatch
                                        TextureRegion))

(defn put! [^MapProperties map-properties k v]
  (.put map-properties k v))

(defn props-get 
  [^MapProperties map-properties k]
  (.get map-properties k))

(defn tile-movement-property
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (get-cell layer (int x) (int y))]
      (let [value (props-get (get-properties (get-tile cell)) "movement")]
        (assert value
                (str "Value for :movement at position "
                     position " / mapeditor inverted position: " [(position 0)
                                                                 (- (dec (props-get (get-properties tiled-map) "height"))
                                                                    (position 1))]
                     " and layer " (get-name layer) " is undefined."))
        value))))

(defn movement-property-layers [tiled-map]
  (->> (get-layers tiled-map)
       reverse
       (filter #(props-get (get-properties %) "movement-properties"))))

(defn movement-properties [tiled-map position]
  (for [layer (movement-property-layers tiled-map)]
    [(get-name layer)
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

(defn static-tiled-map-tile [texture-region]
  (StaticTiledMapTile. ^TextureRegion texture-region))

(defprotocol Visible
  (visible? [_])
  (set-visible! [_ v]))

(extend-type TiledMapTileLayer
  Visible
  (visible? [this]
    (.isVisible this))
  (set-visible! [this v]
    (.setVisible this v)))

(defn tiled-map-tile-layer [width height tilewidth tileheight name visible?]
  (doto (TiledMapTileLayer. (int width)
                            (int height)
                            (int tilewidth)
                            (int tileheight))
    (.setName name)
    (set-visible! visible?)))

(defn add-map-layer! [^MapLayers map-layers ^MapLayer layer]
  (.add map-layers layer))

(defn create-static-tiled-map-tile
  [{:keys [tile/id
           tile/texture-region]}]
  (assert (and id
               texture-region))
  (let [tile (static-tiled-map-tile texture-region)]
    (put! (get-properties tile) "id" id)
    tile))

(defn add-creatures-layer! [tiled-map spawn-positions]
  (let [creature-tile (memoize create-static-tiled-map-tile)
        props (get-properties tiled-map)
        layer (tiled-map-tile-layer (props-get props "width")
                                    (props-get props "height")
                                    (props-get props "tilewidth")
                                    (props-get props "tileheight")
                                    "creatures"
                                    false)]
    (doseq [[[x y] tile] (for [[position creature-property] spawn-positions]
                           [position (creature-tile creature-property)])
            :when tile]
      (set-cell! layer x y tile))
    (add-map-layer! (get-layers tiled-map) layer)))

(import '(com.badlogic.gdx.graphics Color
                                    Colors
                                    Texture
                                    Texture$TextureFilter
                                    OrthographicCamera
                                    Pixmap 
                                    Pixmap$Format))

(defn- draw-tile!
  [x
   y
   ^TiledMapTile tile
   unit-scale
   color-setter
   batch-color
   verts
   batch
   num-vertices]
  (let [region (.getTextureRegion tile)
        x1 (+ x (* (.getOffsetX tile) unit-scale))
        y1 (+ y (* (.getOffsetY tile) unit-scale))
        x2 (+ x1 (* (.getRegionWidth region) unit-scale))
        y2 (+ y1 (* (.getRegionHeight region) unit-scale))
        u1 (.getU region)
        v1 (.getV2 region)
        u2 (.getU2 region)
        v2 (.getV region)
        color11 (float (color-setter batch-color x1 y1))
        color12 (float (color-setter batch-color x1 y2))
        color22 (float (color-setter batch-color x2 y2))
        color21 (float (color-setter batch-color x2 y1))]
    (aset-float verts Batch/X1 x1)
    (aset-float verts Batch/Y1 y1)
    (aset-float verts Batch/C1 color11)
    (aset-float verts Batch/U1 u1)
    (aset-float verts Batch/V1 v1)
    (aset-float verts Batch/X2 x1)
    (aset-float verts Batch/Y2 y2)
    (aset-float verts Batch/C2 color12)
    (aset-float verts Batch/U2 u1)
    (aset-float verts Batch/V2 v2)
    (aset-float verts Batch/X3 x2)
    (aset-float verts Batch/Y3 y2)
    (aset-float verts Batch/C3 color22)
    (aset-float verts Batch/U3 u2)
    (aset-float verts Batch/V3 v2)
    (aset-float verts Batch/X4 x2)
    (aset-float verts Batch/Y4 y1)
    (aset-float verts Batch/C4 color21)
    (aset-float verts Batch/U4 u2)
    (aset-float verts Batch/V4 v1)
    (.draw ^Batch batch
           ^Texture (.getTexture ^TextureRegion region)
           ^floats verts
           (int 0)
           (int num-vertices))))

(defn- draw-tile-layer!
  [^TiledMapTileLayer layer
   ^Batch batch
   view-bounds
   color-setter
   world-unit-scale]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (.getColor batch)
        layer-width (get-width layer)
        layer-height (get-height layer)
        layer-tile-width (* (.getTileWidth layer) world-unit-scale)
        layer-tile-height (* (.getTileHeight layer) world-unit-scale)
        layer-offset-x (* (.getRenderOffsetX layer) world-unit-scale)
        layer-offset-y (* (- (.getRenderOffsetY layer)) world-unit-scale)
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
            (when-let [cell (get-cell layer col row)]
              (when-let [tile (get-tile cell)]
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

(defn draw-tiled-map!
  [tiled-map
   batch
   ^OrthographicCamera camera
   color-setter
   world-unit-scale]
  (let [width  (* (.viewportWidth camera) (.zoom camera))
        height (* (.viewportHeight camera) (.zoom camera))
        up (.up camera)
        w (+ (* width  (Math/abs (float (.y up))))
             (* height (Math/abs (float (.x up)))))
        h (+ (* height (Math/abs (float (.y up))))
             (* width  (Math/abs (float (.x up)))))
        pos (.position camera)
        view-bounds {:x (- (.x pos) (/ w 2))
                     :y (- (.y pos) (/ h 2))
                     :width w
                     :height h}]
    (doseq [layer (filter visible? (get-layers tiled-map))]
      (draw-tile-layer! layer
                        batch
                        view-bounds
                        color-setter
                        world-unit-scale))))

(import '(com.badlogic.gdx.files FileHandle))

(defn- recursively-search [^FileHandle handle extensions]
  (loop [[^FileHandle handle & remaining] (.list handle)
         result []]
    (cond (nil? handle)
          result

          (.isDirectory handle)
          (recur (concat remaining (.list handle)) result)

          (extensions (.extension handle))
          (recur remaining (conj result (.path handle)))

          :else
          (recur remaining result))))

(import '(com.badlogic.gdx ApplicationListener
                           Audio
                           Files
                           Gdx
                           Graphics
                           Input
                           Input$Buttons
                           Input$Keys))

(defn internal [files path]
  (Files/.internal files path))

(defn texture! [^FileHandle file-handle]
  (Texture. file-handle))

(defn pixmap->texture! [pixmap]
  (Texture. ^Pixmap pixmap))

(defn create-textures
  [files {:keys [folder extensions]}]
  (into {}
        (for [path (->> (recursively-search (internal files folder) extensions)
                        (map #(str/replace-first % folder "")))]
          [path (texture! (internal files path))])))

(defn get-texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (let [^Texture texture (safe-get textures file)] 
    (if-let [[x y w h] bounds]
      (TextureRegion. texture (int x) (int y) (int w) (int h))
      (TextureRegion. texture))))

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

(import '(space.earlygrey.shapedrawer ShapeDrawer))

(defmacro with-line-width [shape-drawer width & body]
  `(let [old-line-width# (space.earlygrey.shapedrawer.ShapeDrawer/.getDefaultLineWidth ~shape-drawer)]
     (space.earlygrey.shapedrawer.ShapeDrawer/.setDefaultLineWidth ~shape-drawer (* ~width old-line-width#))
     ~@body
     (space.earlygrey.shapedrawer.ShapeDrawer/.setDefaultLineWidth ~shape-drawer old-line-width#)))

(defn filled-rectangle! [^ShapeDrawer shape-drawer x y w h color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.filledRectangle shape-drawer (float x) (float y) (float w) (float h)))

(defn rectangle! [^ShapeDrawer shape-drawer x y w h color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.rectangle shape-drawer x y w h))

(defn line! [^ShapeDrawer shape-drawer [sx sy] [ex ey] color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.line shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn circle! [^ShapeDrawer shape-drawer [x y] radius color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.circle shape-drawer x y radius))

(defn ellipse! [^ShapeDrawer shape-drawer [x y] radius-x radius-y color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.ellipse shape-drawer x y radius-x radius-y))

(defn filled-circle! [^ShapeDrawer shape-drawer [x y] radius color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.filledCircle shape-drawer (float x) (float y) (float radius)))

(defn sector! [^ShapeDrawer shape-drawer [center-x center-y] radius start-radians radians color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.sector shape-drawer center-x center-y radius start-radians radians))

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

(import '(clojure RayCaster))

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

(import '(com.badlogic.gdx.scenes.scene2d.ui Button 
                                             ButtonGroup
                                             Cell
                                             CheckBox 
                                             HorizontalGroup 
                                             Image 
                                             ImageButton 
                                             Label 
                                             ScrollPane
                                             SelectBox
                                             Skin 
                                             Stack 
                                             Table 
                                             TextButton 
                                             TextField
                                             TextTooltip 
                                             TooltipManager
                                             Widget 
                                             Window))

(defn text-tooltip [^String text ^Skin skin]
  (TextTooltip. text skin))

(import '(com.badlogic.gdx.scenes.scene2d Actor
                                          Event
                                          Group
                                          Stage
                                          Touchable))

(import '(com.badlogic.gdx.scenes.scene2d.utils ChangeListener
                                                ClickListener
                                                Drawable
                                                TextureRegionDrawable
                                                Layout))

(extend-type Actor
  GetWidth
  (get-width [this]
    (.getWidth this))
  GetHeight
  (get-height [this]
    (.getHeight this)))

(defn create-actor [{:keys [act! draw!]}]
  (proxy [Actor] []
    (act [delta]
      (when act!
        (act! this delta))
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (when draw!
        (draw! batch parent-alpha)))))

(defn get-stage [^Actor actor]
  (.getStage actor))

(defn set-text! [^Label label ^String text]
  (Label/.setText label text))

(defn- set-label-text-actor [label-widget text-fn]
  (create-actor
    {:act! 
     (fn [this delta]
       (when (get-stage this)
         (set-text! label-widget (text-fn))))}))

(defn pack! [layout]
  (Layout/.pack layout))

(defprotocol AddActor
  (add-actor! [_ actor]))

(extend-type Group
  AddActor
  (add-actor! [this actor]
    (.addActor this actor)))

(extend-type Stage
  AddActor
  (add-actor! [this actor]
    (.addActor this actor)))

(defn texture-image
  [^Texture texture]
  (Image. texture))

(defn drawable-image
  [^Drawable drawable]
  (Image. drawable))

(defn texture-region-image 
  [^TextureRegion texture-region]
  (Image. texture-region))

(defn add! [^Table table ^Actor actor]
  (.add table actor))

(defn row! [^Table table]
  (.row table))

(defn label-create [^String text ^Skin skin]
  (Label. text skin))

(defn table-create []
  (Table.))

(defn bottom! [cell]
  (.bottom ^Cell cell))

(defn right! [cell]
  (.right ^Cell cell))

(defn expand! [cell]
  (.expand ^Cell cell))

(defn expand-x! [cell]
  (.expandX ^Cell cell))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (label-create "" skin)
         sub-table (table-create)]
     (doseq [cell [{:actor (texture-image icon)}
                   {:actor label}]]
       (add! sub-table (:actor cell)))
     (row! sub-table)
     (pack! sub-table)
     (add-actor! table (set-label-text-actor label text-fn))
     (doto (add! table sub-table)
       (right!)
       (expand-x!))))
  ([skin table text-fn]
   (let [label (label-create "" skin)]
     (add-actor! table (set-label-text-actor label text-fn))
     (doto (add! table label)
       (right!)
       (expand-x!)))))

(defn create-window [^String title skin]
  (Window. title ^Skin skin))

(defn add-listener! [actor listener]
  (Actor/.addListener actor listener))

(defn create-change-listener [f]
  (proxy [ChangeListener] []
    (changed [event actor]
      (f event actor))))

(defn create-text-button [text skin]
  (TextButton. ^String text ^Skin skin))

(defn remove! [^Actor actor]
  (.remove actor))

(defn get-title-table [window]
  (Window/.getTitleTable window))

(defn- main-table [^Skin skin menus update-labels]
  (let [table (table-create)]
    (doseq [cell (for [{:keys [label items]} menus]
                   {:actor
                    (doto (create-text-button label skin)
                      (add-listener! (create-change-listener
                                       (fn [event actor]
                                         (let [^Stage stage (.getStage ^Event event)]
                                           (add-actor! stage
                                                      (let [window (create-window label skin)]
                                                        (doseq [{:keys [label on-click]} items]
                                                          (add! window (doto (create-text-button label skin)
                                                                         (add-listener! (create-change-listener
                                                                                          (fn [_event _actor]
                                                                                            (on-click)))))))
                                                        (row! window)
                                                        (pack! window)
                                                        (add! (get-title-table window)
                                                              (doto (create-text-button "X" skin)
                                                                (add-listener! (create-change-listener
                                                                                 (fn [_event _actor]
                                                                                   (remove! window))))))
                                                        window)))))))})]
      (add! table (:actor cell)))
    (row! table)
    (pack! table)
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn set-touchable! [actor touchable]
  (Actor/.setTouchable actor touchable))

(defn fill-x! [cell]
  (.fillX ^Cell cell))

(defn colspan! [cell n]
  (.colspan ^Cell cell (int n)))

(defn fill-y! [cell]
  (.fillY ^Cell cell))

(defn set-fill-parent! [table flag?]
  (Table/.setFillParent table true))

(defn create-dev-menu
  [{:keys [menus update-labels skin]}]
  (let [table (table-create)]
    (doto (add! table (main-table skin menus update-labels))
      (expand-x!)
      (fill-x!)
      (colspan! 1))
    (row! table)
    (doto (add! table (doto (label-create "" skin)
                        (set-touchable! Touchable/disabled)))
      (expand!)
      (fill-x!)
      (fill-y!))
    (row! table)
    (doto table
      (pack!)
      (set-fill-parent! true))))

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

(require '[clojure.pprint :as pprint])

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

(declare build-values)

(defn- create-value [schemas [k] v db]
  (case k
    :s/map
    (build-values schemas v db)

    :s/one-to-many
    (set (map (fn [property-id]
                (build-values schemas (safe-get db property-id) db))
              v))

    :s/one-to-one
    (build-values schemas (safe-get db v) db)

    v))

(defn build-values [schemas property db]
  (reduce (fn [m k]
            (assoc m k
                   (try (create-value schemas (get schemas k) (k m) db)
                        (catch Throwable t
                          (throw (ex-info " " {:k k
                                               :v (k m)} t))))))
          property
          (keys property)))

(import '(com.badlogic.gdx.math Circle Intersector Rectangle Vector2 Vector3))

(defn create-stack []
  (Stack.))

(import '(com.badlogic.gdx.utils.viewport FitViewport Viewport))

(defn unproject [^Viewport viewport [x y]]
  (let [v2 (Vector2. x y)]
    (.unproject viewport v2)
    [(.x v2) (.y v2)]))

(defn get-user-object [^Actor actor]
  (.getUserObject actor))

(defn get-viewport [^Stage stage]
  (.getViewport stage))

(defprotocol GetX
  (get-x [_]))

(extend-type Actor
  GetX
  (get-x [this]
    (.getX this)))

(extend-type Input
  GetX
  (get-x [this]
    (.getX this)))

(defprotocol GetY
  (get-y [_]))

(extend-type Actor
  GetY
  (get-y [this]
    (.getY this)))

(extend-type Input
  GetY
  (get-y [this]
    (.getY this)))

(defn get-parent [actor]
  (.getParent ^Actor actor))

(defn inventory-window-cell-highlight-widget [draw-cell-rect! input player-eid]
  (proxy [Widget] []
    (draw [_batch _parent-alpha]
      (when-let [stage (.getStage ^Actor this)]
        (draw-cell-rect! @player-eid
                         (get-x this)
                         (get-y this)
                         (let [[x y] (unproject (get-viewport stage) [(get-x input) (get-y input)])
                               local (.stageToLocalCoordinates ^Actor this (Vector2. x y))
                               x (.x ^Vector2 local)
                               y (.y ^Vector2 local)]
                           (.hit ^Actor this (float x) (float y) true))
                         (get-user-object (get-parent this)))))))

(defn set-user-object! [^Actor actor obj]
  (.setUserObject actor obj))

(defn set-name! [^Actor actor name]
  (.setName actor name))

(defn inventory-window-cell-image-widget [background-drawable cell-size]
  (doto (drawable-image background-drawable)
    (set-name! "image-widget")
    (set-user-object! {:background-drawable background-drawable
                       :cell-size cell-size})))

(defn add-actors! [group actors]
  (run! #(add-actor! group %) actors))

(defn- inv-window-cell [player-eid input on-click-cell slot->drawable draw-cell-rect! cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    (doto (create-stack)
      (add-actors! [(inventory-window-cell-highlight-widget draw-cell-rect! input player-eid)
                    (inventory-window-cell-image-widget background-drawable cell-size)])
      (add-listener! (proxy [ClickListener] []
                       (clicked [event _x _y]
                         (on-click-cell event cell))))
      (set-name! "inventory-cell")
      (set-user-object! cell))))

(defn pad! [cell fv]
  (.pad ^Cell cell (float fv)))

(defn pad-top! [cell fv]
  (.padTop ^Cell cell (float fv)))

(defn pad-bottom! [cell fv]
  (.padBottom ^Cell cell (float fv)))

(defn expand-y! [cell]
  (.expandY ^Cell cell))

(extend-type Actor
  Visible
  (visible? [this]
    (.isVisible this))
  (set-visible! [this v]
    (.setVisible this v)))

(defn toggle-visible! [obj]
  (set-visible! obj (not (visible? obj))))

(defn set-position! 
  ([actor x y]
   (Actor/.setPosition actor (float x) (float y)))
  ([actor x y align]
   (Actor/.setPosition actor (float x) (float y) align)))

(defn- build-inventory-window
  [{:keys [player-eid
           input
           on-click-cell
           draw-cell-rect!
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (TextureRegionDrawable. ^TextureRegion (slot->texture-region slot))
                           (.setMinSize cell-size cell-size)
                           (.tint (Color. 1 1 1 0.4))))
        ->cell (partial inv-window-cell player-eid input on-click-cell slot->drawable draw-cell-rect! cell-size)
        cell-table (table-create)
        window (create-window "Inventory" skin)]
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
        (add! cell-table actor))
      (row! cell-table))
    (pack! cell-table)
    (set-name! cell-table "inventory-cell-table")
    (let [c (add! window cell-table)]
      (pad! c 4))
    (row! window)
    (pack! window)
    (set-name! window "moon.ui.windows.inventory")
    (set-visible! window false)
    (let [[x y] position]
      (set-position! window x y))
    window))

(defn get-world-width [^Viewport viewport]
  (.getWorldWidth viewport))

(defn get-world-height [^Viewport viewport]
  (.getWorldHeight viewport))

(defn create-inventory-window
  [slot->texture-region player-eid input on-click-cell draw-cell-rect! skin stage]
  (build-inventory-window
    {:player-eid player-eid
     :input input
     :on-click-cell on-click-cell
     :draw-cell-rect! draw-cell-rect!
     :skin skin
     :position [(get-world-width (get-viewport stage))
                (get-world-height (get-viewport stage))]
     :slot->texture-region slot->texture-region
     :cell-size 48}))

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
    (java.util.Collections/shuffle al ^Random random)
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
        (let [current-order (if (< (next-float random) turn-ratio)
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
        props (get-properties modules-tiled-map)
        _ (assert (and (= (props-get props "width")
                          (* number-modules-x (+ modules-width module-offset-tiles)))
                       (= (props-get props "height")
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
  (let [copy-tile (memoize
                   (fn [tile]
                     (assert tile)
                     (if (instance? StaticTiledMapTile tile)
                       (StaticTiledMapTile. ^StaticTiledMapTile tile)
                       (static-tiled-map-tile tile))))]
    {:properties (merge (let [props (get-properties schema-tiled-map)]
                          (zipmap (get-keys props)
                                  (get-values props)))
                        {"width" (count grid)
                         "height" (count (grid 0))})
     :layers (for [layer (get-layers schema-tiled-map)]
               {:name (get-name layer)
                :visible? (visible? layer)
                :properties (let [props (get-properties layer)]
                              (zipmap (get-keys props)
                                      (get-values props)))
                :tiles (for [x (range (count grid))
                             y (range (count (grid 0)))
                             :let [position [x y]
                                   local-position (get-in grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (get-cell layer (local-position 0) (local-position 1))]
                             [position (copy-tile (get-tile cell))])))})}))

(defn- create-tiled-map* [schema-tiled-map scaled-grid]
  (let [{:keys [properties layers]} (grid->tiled-map schema-tiled-map scaled-grid)
        tiled-map (TiledMap.)]
    (doseq [[k v] properties]
      (assert (string? k))
      (put! (get-properties tiled-map) k v))
    (doseq [{:keys [name visible? properties tiles]} layers]
      (assert (string? name))
      (assert (boolean? visible?))
      (let [props (get-properties tiled-map)
            layer (tiled-map-tile-layer (props-get props "width")
                                        (props-get props "height")
                                        (props-get props "tilewidth")
                                        (props-get props "tileheight")
                                        name 
                                        visible?)]
        (doseq [[k v] properties]
          (assert (string? k))
          (put! (get-properties layer) k v))
        (doseq [[[x y] tile] tiles
                :when tile]
          (set-cell! layer x y tile))
        (add-map-layer! (get-layers tiled-map) layer)))
    tiled-map))

(defn create-modules-level
  [{:keys [level/creature-properties
           world/map-size
           world/spawn-rate]
    :or {map-size 5
         spawn-rate 0.05}}]
  (let [scale [32 20]
        {:keys [start grid]} (create-caves (create-random) map-size map-size :wide)
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
        schema-tiled-map (.load (TmxMapLoader.) "maps/modules.tmx")
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
        tiled-map (TiledMap.)]
    (doseq [[k v] properties]
      (assert (string? k))
      (put! (get-properties tiled-map) k v))
    (doseq [{:keys [name visible? properties tiles]} layers]
      (assert (string? name))
      (assert (boolean? visible?))
      (let [props (get-properties tiled-map)
            layer (tiled-map-tile-layer (props-get props "width")
                                        (props-get props "height")
                                        (props-get props "tilewidth")
                                        (props-get props "tileheight")
                                        name 
                                        visible?)]
        (doseq [[k v] properties]
          (assert (string? k))
          (put! (get-properties layer) k v))
        (doseq [[[x y] tile] tiles
                :when tile]
          (set-cell! layer x y tile))
        (add-map-layer! (get-layers tiled-map) layer)))
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
        {:keys [start grid]} (create-caves (create-random) cave-size cave-size cave-style)
        _ (assert (= #{:wall :ground} (set (apply concat grid))))
        grid (fix-nads grid)
        create-tile (let [texture (get textures texture-path)]
                      (memoize
                       (fn [& {:keys [sprite-idx movement]}]
                         {:pre [#{"all" "air" "none"} movement]}
                         (let [texture-region (TextureRegion. ^Texture texture
                                                              (int (* (sprite-idx 0) tile-size))
                                                              (int (* (sprite-idx 1) tile-size))
                                                              (int tile-size)
                                                              (int tile-size))
                               tile (static-tiled-map-tile texture-region)]
                           (put! (get-properties tile) "movement" movement)
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

(defn find-actor [^Group group actor-name]
  (.findActor group actor-name))

(defn get-children [^Group group]
  (.getChildren group))

(defn- inv-get-cell [inventory-window cell]
  (->> (get-children (find-actor inventory-window "inventory-cell-table"))
       (filter #(= (get-user-object %) cell))
       first))

(defn set-item!* [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (inv-get-cell inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")
        cell-size (:cell-size (get-user-object image-widget))]
    (.setDrawable ^Image image-widget ^Drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                                  (.setMinSize cell-size cell-size)))
    (add-listener! cell-widget (text-tooltip tooltip-text skin))
    nil))

(defn item-info-text [item]
  (assert (valid-item? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (mods-format-text (:stats/modifiers item)) "[]"))])))

(defn get-root [^Stage stage]
  (.getRoot stage))

(defn add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        horizontal-group (find-actor action-bar "moon.ui.action-bar.horizontal-group")
        button-group (get-user-object horizontal-group)
        button (doto (ImageButton.
                      (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                        (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                                     (* scale (.getRegionHeight ^TextureRegion texture-region)))))
                 (add-listener! (text-tooltip tooltip-text skin))
                 (set-user-object! skill-id))]
    (add-actor! horizontal-group ^Actor button)
    (.add ^ButtonGroup button-group ^Button button)
    nil))

#_(defn remove-skill!
  [action-bar skill-id]
  (let [horizontal-group (find-actor action-bar "moon.ui.action-bar.horizontal-group")
        button-group (get-user-object horizontal-group)
        button (get horizontal-group skill-id)]
    (remove! button)
    (.remove ^ButtonGroup button-group ^Button button)
    nil))

(defn remove-item!* [inventory-window cell]
  (let [cell-widget (inv-get-cell inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")]
    (.setDrawable ^Image image-widget ^Drawable (:background-drawable (get-user-object image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn remove-item! [stage cell]
  (-> (get-root stage)
      (find-actor "moon.ui.windows.inventory")
      (remove-item!* cell)))

(defn toggle-inventory-visible! [stage]
  (-> (get-root stage)
      (find-actor "moon.ui.windows.inventory")
      toggle-visible!))

(defn close-windows! [stage]
  (->> (get-children (find-actor (get-root stage) "moon.ui.windows"))
       (run! #(set-visible! % false))))

(defn toggle-entity-info! [stage]
  (-> (get-root stage)
      (find-actor "moon.ui.windows.entity-info")
      toggle-visible!))

(defn mouseover-actor [stage x y]
  (.hit ^Stage stage (float x) (float y) true))

(defn ui-mouse-position [stage input]
  (unproject (get-viewport stage) 
             [(get-x input) (get-y input)]))

(import '(com.badlogic.gdx.utils Align
                                 Disposable
                                 ScreenUtils))

(defn set-modal! [window modal?]
  (Window/.setModal window modal?))

(defn show-modal! [^Skin skin ^Stage stage {:keys [title text button-text on-click]}]
  (assert (not (find-actor (get-root stage) "moon.ui.modal-window")))
  (let [^String title title
        ^String text text
        window (create-window title skin)]
    (add! window (label-create text skin))
    (row! window)
    (add! window (doto (create-text-button button-text skin)
                   (add-listener! (create-change-listener
                                    (fn [_event _actor]
                                      (remove! (find-actor (get-root stage) "moon.ui.modal-window"))
                                      (on-click))))))
    (row! window)
    (pack! window)
    (set-modal! window true)
    (set-name! window "moon.ui.modal-window")
    (set-position! window
                  (/ (get-world-width (get-viewport stage)) 2)
                  (* (get-world-height (get-viewport stage)) (/ 3 4))
                  Align/center)
    (add-actor! stage window)))

(defn- create-info-window
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [^Skin skin skin
        label (label-create "MY LABEL TEXT" skin)
        window (create-window title skin)
        c (add! window label)]
    (expand! c)
    (row! window)
    (pack! window)
    (set-name! window actor-name)
    (set-visible! window visible?)
    (let [[x y] position]
      (set-position! window x y))
    (add-actor! window (proxy [Actor] []
                         (act [delta]
                           (let [^Actor this this]
                             (when (.getStage this)
                               (set-text! label (set-label-text!)))
                             (pack! window)
                             (proxy-super act delta)))
                         (draw [batch parent-alpha])))
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

(defn float-bits [r g b a]
  (Color/toFloatBits (float r) (float g) (float b) (float a)))

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
    (Rectangle. (float x) (float y) (float width) (float height))))

(defn overlaps? [entity other-entity]
  (.overlaps ^Rectangle (rectangle entity)
             ^Rectangle (rectangle other-entity)))

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
    (filter #(.contains ^Rectangle (rectangle @%) (float (first pos)) (float (second pos)))
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

(defn- create-grid [^TiledMap tiled-map]
  (let [props (get-properties tiled-map)
        w (props-get props "width")
        h (props-get props "height")
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
  (let [button-class? (fn [a] (some #(= Button %) (supers (class a))))]
    (or (button-class? actor)
        (when-let [parent (get-parent actor)]
          (button-class? parent)))))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (get-parent actor)
                            (= "inventory-cell" (get-name (get-parent actor)))
                            (get-user-object (get-parent actor)))]
    (cond
      inventory-slot
      [:mouseover-actor/inventory-cell inventory-slot]

      (when (instance? Label actor)
        (when-let [p (get-parent actor)]
          (when-let [p (get-parent p)]
            (and (instance? Window p)
                 (= (.getTitleLabel ^Window p) actor)))))
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

(defn dispose! [disposable]
  (Disposable/.dispose disposable))

(def rgba8888 Pixmap$Format/RGBA8888)

(defn pixmap 
  ([file-handle]
   (Pixmap. ^FileHandle file-handle))
  ([width height pixmap-format]
   (Pixmap. (int width) (int height) ^Pixmap$Format pixmap-format)))

(defn- white-pixel-texture []
  (let [pixmap (doto (pixmap 1 1 rgba8888)
                 (Pixmap/.setColor 1 1 1 1)
                 (Pixmap/.drawPixel 0 0))
        texture (pixmap->texture! pixmap)]
    (dispose! pixmap)
    texture))

(defn- draw-text! 
  [batch 
   default-font 
   unit-scale 
   {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        scale (or scale 1)
        font-data (.getData ^BitmapFont font)
        old-scale (.scaleX ^BitmapFont$BitmapFontData font-data)
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (.setScale ^BitmapFont$BitmapFontData font-data (* old-scale scale))
    (.draw ^BitmapFont font
           ^Batch batch
           text
           (float x)
           (float (+ y (if up?
                         (-> text
                             (str/split #"\n")
                             count
                             (* (.getLineHeight ^BitmapFont font)))
                         0)))
           (float target-width)
           Align/center
           wrap?)
    (.setScale ^BitmapFont$BitmapFontData font-data old-scale)))

(defn- draw-texture-region! [batch unit-scale texture-region [x y] & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(.getRegionWidth ^TextureRegion texture-region)
                                (.getRegionHeight ^TextureRegion texture-region)]]
                  (if (= unit-scale 1)
                    dimensions
                    (mapv (comp float (partial * unit-scale))
                          dimensions)))]
    (if center?
      (Batch/.draw ^Batch batch
                   ^TextureRegion texture-region
                   (float (- (float x) (/ (float w) 2)))
                   (float (- (float y) (/ (float h) 2)))
                   (float (/ (float w) 2))
                   (float (/ (float h) 2))
                   (float w)
                   (float h)
                   (float 1)
                   (float 1)
                   (float (or rotation 0)))
      (.draw ^Batch batch
             ^TextureRegion texture-region
             (float x)
             (float y)
             (float w)
             (float h)))))

(defn- item-place-position [world-mouse-position player-position maxrange]
  (mapv + player-position
        (v2-scale (v2-direction player-position world-mouse-position)
                  (min maxrange
                       (v2-distance player-position world-mouse-position)))))

(defn- spawn-creature [{:keys [position creature-property components]}]
  (assert creature-property)
  (let [entity (-> creature-property
                   (assoc :entity/position position
                          :entity/collides? true
                          :entity/z-order (if (:entity/flying? creature-property)
                                            :z-order/flying
                                            :z-order/ground))
                   (assoc :entity/destroy-audiovisual :audiovisuals/creature-die))]
    (assert (not-any? #(contains? entity %) (keys components)))
    (merge entity components)))

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

(defn- spawn-alert [position faction duration elapsed-time]
  (spawn-effect position
                {:entity/alert-friendlies-after-duration
                 {:counter (timer-create duration elapsed-time)
                  :faction faction}}))

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
   :entity/movement {:direction direction :speed speed}
   :entity/image image
   :entity/faction faction
   :entity/delete-after-duration (/ max-range speed)
   :entity/destroy-audiovisual :audiovisuals/hit-wall
   :entity/projectile-collision {:entity-effects entity-effects
                                 :piercing? piercing?}})

(defn- create-fsm
  [fsm initial-state]
  (assoc (fsm initial-state nil) :state initial-state))

(declare effect-applicable?)

(def effects-behaviour
  {
   :effects/projectile {
                        :applicable? 
                        (fn [_v effect-ctx]
                          (:effect/target-direction effect-ctx))
                        }
   :effects/spawn {
                   :applicable?
                   (fn [_v effect-ctx]
                     (and (:entity/faction @(:effect/source effect-ctx))
                          (:effect/target-position effect-ctx)))
                   }
   :effects/target-all {
                        :applicable? 
                        (fn [_v _effect-ctx] 
                          true)
                        }
   :effects/target-entity {
                           :applicable? 
                           (fn [v effect-ctx]
                             (and (:effect/target effect-ctx)
                                  (seq (filter #(effect-applicable? % effect-ctx) (:entity-effects v)))))
                           }
   :effects.target/convert {
                            :applicable?
                            (fn [_v effect-ctx]
                              (let [source (:effect/source effect-ctx)
                                    target (:effect/target effect-ctx)]
                                (and target
                                     (= (:entity/faction @target)
                                        (enemy (:entity/faction @source))))))
                            }

   :effects.target/damage {
                           :applicable? 
                           (fn [_v effect-ctx] 
                             (and (:effect/target effect-ctx)
                                  #_(:stats/hp @target)))
                           }

   :effects.target/kill {
                         :applicable? 
                         (fn [_v effect-ctx] 
                           (and (:effect/target effect-ctx)
                                (:entity/fsm @(:effect/target effect-ctx))))
                         }

   :effects.target/melee-damage {
                                 :applicable? 
                                 (fn [_v effect-ctx] 
                                   ; TODO call damage-effect-applicable ... 
                                   (effect-applicable? [:effects.target/damage (melee-damage @(:effect/source effect-ctx))]
                                                       effect-ctx))
                                 }

   :effects.target/spiderweb {
                              :applicable? 
                              (fn [_v effect-ctx] 
                                (:entity/stats @(:effect/target effect-ctx)))
                              }

   :effects.target/stun {
                         :applicable? 
                         (fn [v effect-ctx]
                           (and (:effect/target effect-ctx)
                                (:entity/fsm @(:effect/target effect-ctx))))
                         }

   })

(defn- effect-applicable?
  [[k v] effect-ctx]
  ((:applicable? (safe-get effects-behaviour k)) v effect-ctx))

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

(defn- effect-useful?
  [[k v] effect-ctx ray-blocked?]
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

(defn- affected-targets
  [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(def ^:private spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def ^:private spiderweb-duration 5)

(defn projectile-effect [v effect-ctx]
  (let [source (:effect/source effect-ctx)
        source* @source
        direction (:effect/target-direction effect-ctx)
        size (:projectile/size v)]
    [[:tx/spawn-entity (spawn-projectile
                         {:position (mapv + (:entity/position source*)
                                          (v2-scale direction
                                                    (+ (/ (:entity/width source*) 2) size 0.1)))
                          :direction direction
                          :faction (:entity/faction source*)}
                         v)]]))

(defn animation-entity [game-db position animation-k]
  (assert (keyword? animation-k))
  (let [{:keys [entity/animation]} (safe-get game-db animation-k)]
    (spawn-effect position
                  {:entity/animation (assoc animation :delete-after-stopped? true)})))

(defn spawn-effect* [v effect-ctx game-db fsms]
  (let [source (:effect/source effect-ctx)
        position (:effect/target-position effect-ctx)]
    [[:tx/sound "bfxr_curse.wav"]
     [:tx/spawn-entity (animation-entity game-db position :audiovisuals/spawn)]
     [:tx/spawn-entity (spawn-creature {:position position
                                        :creature-property v
                                        :components {:entity/fsm {:fsm (:npc fsms)
                                                                  :initial-state :npc-idle}
                                                     :entity/faction (:entity/faction @source)}})]]))

(defn target-all-effect [v effect-ctx active-entities raycaster applicable-effect-txs]
  (let [source (:effect/source effect-ctx)
        source* @source]
    (mapcat (fn [target]
              (cons [:tx/spawn-entity 
                     (spawn-line {:start (:entity/position source*)
                                  :end (:entity/position @target)
                                  :duration 0.05
                                  :color (float-bits 1 0 0 0.75)
                                  :thick? true})]
                    (applicable-effect-txs {:effect/source source
                                            :effect/target target}
                                           (:entity-effects v))))
            (affected-targets active-entities raycaster source*))))

(defn convert-effect [_v effect-ctx game-db]
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)]
    [[:tx/sound "bfxr_burrow.wav"]
     [:tx/spawn-entity (animation-entity game-db (:entity/position @target) :audiovisuals/convert)]
     [:tx/swap target assoc :entity/faction (:entity/faction @source)]]))

(defn target-entity-effect [v effect-ctx game-db applicable-effect-txs]
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)
        body        @source
        target-body @target
        {:keys [maxrange entity-effects]} v]
    (if (in-range? body target-body maxrange)
      (cons [:tx/spawn-entity 
             (spawn-line
               {:start (start-point body target-body)
                :end (:entity/position target-body)
                :duration 0.05
                :color (float-bits 1 0 0 0.75)
                :thick? true})]
            (applicable-effect-txs effect-ctx entity-effects))
      [[:tx/sound "bfxr_fisthit.wav"]
       [:tx/spawn-entity (animation-entity game-db (end-point body target-body maxrange) :audiovisuals/hit-ground)]])))

(defn damage-effect [v effect-ctx game-db elapsed-time]
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
      [[:tx/swap target add-text-effect "[WHITE]ARMOR" 0.3 #(timer-create % elapsed-time)]]

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
        [[:tx/swap target assoc-in [:entity/stats :stats/hp 0] new-hp-val]
         [:tx/swap target add-text-effect dmg-text 0.3 #(timer-create % elapsed-time)]
         [:tx/event target (if (zero? new-hp-val) :kill :alert)]
         [:tx/sound "bfxr_normalhit.wav"]
         [:tx/spawn-entity (animation-entity game-db (:entity/position target*) :audiovisuals/damage)]]))))

(defn kill-effect [_v effect-ctx]
  [[:tx/event (:effect/target effect-ctx) :kill]])

(defn spiderweb-effect [_v effect-ctx elapsed-time]
  (let [target (:effect/target effect-ctx)]
    ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
    (when-not (:entity/temp-modifier @target)
      [[:tx/swap target assoc :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                     :counter (timer-create spiderweb-duration elapsed-time)}]
       [:tx/swap target update :entity/stats add-mods spiderweb-modifiers]])))

(defn stun-effect [duration effect-ctx]
  [[:tx/event (:effect/target effect-ctx) :stun duration]])

(defn- usable-state
  [{:keys [skill/cooling-down? skill/effects] :as skill}
   entity
   effect-ctx]
  (cond
   cooling-down?
   :cooldown

   (> (:skill/cost skill) ((get-mana (:entity/stats entity)) 0))
   :not-enough-mana

   (not (seq (filter #(effect-applicable? % effect-ctx) effects)))
   :invalid-params

   :else
   :usable))

(defn create-skin [^FileHandle file-handle]
  (Skin. file-handle))

(defn create-skin! [file-handle]
  (let [s (create-skin file-handle)]
    (set! (.markupEnabled ^BitmapFont$BitmapFontData (.getData (.getFont ^Skin s "default-font"))) true)
    s))

(defn new-cursor [graphics pixmap hotspot-x hotspot-y]
  (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y))

(defn create-viewport! [width height world-unit-scale]
  (let [world-width (* width world-unit-scale)
        world-height (* height world-unit-scale)]
    (FitViewport. (float world-width)
                  (float world-height)
                  (doto (OrthographicCamera.)
                    (.setToOrtho false world-width world-height)))))

(import '(com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter))

(defn create-default-font! [files {:keys [path size quality-scaling use-integer-positions?]}]
  (let [generator (FreeTypeFontGenerator. (internal files path))
        parameter (let [p (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                    (set! (.size p) (* size quality-scaling))
                    (set! (.minFilter p) Texture$TextureFilter/Linear)
                    (set! (.magFilter p) Texture$TextureFilter/Linear)
                    p)
        font (.generateFont generator parameter)
        font-data (.getData font)]
    (dispose! generator)
    (.setScale font-data (/ quality-scaling))
    (set! (.markupEnabled font-data) true)
    (.setUseIntegerPositions font use-integer-positions?)
    font))

(defn create-vampire-level [_]
  {:tiled-map (.load (TmxMapLoader.) "maps/vampire.tmx")
   :start-position [32 71]})

(defn spawn-positions [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (layers-get (get-layers tiled-map) layer-name)]
    (for [x (range (get-width layer))
          y (range (get-height layer))
          :let [cell (get-cell layer x y)]
          :when cell
          :let [value (props-get (get-properties (get-tile cell)) property-key)]
          :when value]
      [[x y] value])))

(defn build [schemas db property-id]
  (build-values schemas (safe-get db property-id) db))

(defn spawn-map-creatures [schemas db tiled-map start-position fsms]
  (mapcat (fn [[position creature-id]] 
            (when (not= position start-position)
              [[:tx/spawn-entity 
               (spawn-creature {:position (mapv (partial + 0.5) position)
                                :creature-property (build schemas db (keyword creature-id))
                                :components {:entity/fsm {:fsm (:npc fsms)
                                                          :initial-state :npc-sleeping}
                                             :entity/faction :evil}})]]))
          (spawn-positions tiled-map)))

(defn handle-clicked-inventory-cell
  [player-eid cell]
  (case (:state (:entity/fsm @player-eid))
    :player-idle
    (when-let [item (item-at (:entity/inventory @player-eid) cell)]
      [[:tx/sound "bfxr_takeit.wav"]
       [:tx/swap player-eid remove-item cell]
       [:tx/remove-item cell]
       [:tx/event player-eid :pickup-item item]])

    :player-item-on-cursor
    (let [entity @player-eid
          inventory (:entity/inventory entity)
          item-in-cell (item-at inventory cell)
          item-on-cursor (:entity/item-on-cursor entity)]
      (cond
       (and (not item-in-cell)
            (valid-slot? cell item-on-cursor))
       [[:tx/swap player-eid dissoc :entity/item-on-cursor]
        [:tx/sound "bfxr_itemput.wav"]
        [:tx/swap player-eid set-item cell item-on-cursor]
        [:tx/set-item cell item-on-cursor]
        [:tx/event player-eid :dropped-item]]

       (and item-in-cell
            (valid-slot? cell item-on-cursor))
       [[:tx/swap player-eid dissoc :entity/item-on-cursor]
        [:tx/sound "bfxr_itemput.wav"]
        [:tx/swap player-eid remove-item cell]
        [:tx/remove-item cell]
        [:tx/swap player-eid set-item cell item-on-cursor]
        [:tx/set-item cell item-on-cursor]
        [:tx/event player-eid :dropped-item]
        [:tx/event player-eid :pickup-item item-in-cell]]))

    nil))

(defn player-message-actor-create [default-font unit-scale]
  (let [message-duration-seconds 0.5]
    (doto (proxy [Actor] []
            (act [delta]
              (let [state (get-user-object this)]
                (when (:text @state)
                  (swap! state update :counter + delta)
                  (when (>= (:counter @state) message-duration-seconds)
                    (reset! state nil))))
              (let [^Actor this this]
                (proxy-super act delta)))
            (draw [batch parent-alpha]
              (when-let [stage (.getStage ^Actor this)]
                (let [state (get-user-object this)
                      vp-width (get-world-width (get-viewport stage))
                      vp-height (get-world-height (get-viewport stage))]
                  (when-let [text (:text @state)]
                    (draw-text! batch 
                                default-font 
                                @unit-scale 
                                {:x (/ vp-width 2)
                                 :y (+ (/ vp-height 2) 200)
                                 :text text
                                 :scale 2.5
                                 :up? true}))))))
      (set-name! "player-message")
      (set-user-object! (atom nil)))))

(defn- entity-state-draw-ui-view
  [[k _v] eid batch unit-scale image->texture-region mouseover-actor* ui-mouse-pos]
  (case k
    :player-item-on-cursor
    (when mouseover-actor*
      (draw-texture-region! batch
                            @unit-scale
                            (image->texture-region (:entity/image (:entity/item-on-cursor @eid)))
                            ui-mouse-pos
                            {:center? true}))

    nil))

(defn player-state-draw-create [player-eid unit-scale image->texture-region input]
  (proxy [Actor] []
    (act [delta]
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (let [stage (.getStage ^Actor this)
            player-eid player-eid 
            entity @player-eid
            state-k (:state (:entity/fsm entity))
            mouse-pos (ui-mouse-position stage input)
            [x y] mouse-pos]
        (entity-state-draw-ui-view [state-k (state-k entity)]
                                   player-eid
                                   batch
                                   unit-scale
                                   image->texture-region
                                   (mouseover-actor stage x y)
                                   mouse-pos)))))

(defn windows-create [actors]
  (doto (Group.)
    (add-actors! actors)
    (set-name! "moon.ui.windows")))

(defn get-camera [^Viewport viewport]
  (.getCamera viewport))

(defn draw-cell-debug
  [factions-iterations shape-drawer viewport show-cell-entities? show-cell-occupied? show-potential-field-colors? grid]
  (let [^OrthographicCamera camera (get-camera viewport)
        plane-points (mapv (fn [^Vector3 v3]
                             [(.x v3) (.y v3) (.z v3)])
                           (.planePoints (.frustum camera)))
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
        (filled-rectangle! shape-drawer x y 1 1 (float-bits 1 0 0 0.6)))
      (when (and show-cell-occupied? (seq (:occupied cell*)))
        (filled-rectangle! shape-drawer x y 1 1 (float-bits 0 0 1 0.6)))
      (when-let [faction show-potential-field-colors?]
        (let [{:keys [distance]} (faction cell*)]
          (when distance
            (let [ratio (/ distance (factions-iterations faction))]
              (filled-rectangle! shape-drawer x y 1 1 (float-bits ratio (- 1 ratio) ratio 0.6)))))))))

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
  [shape-drawer viewport show-tile-grid?]
  (when show-tile-grid?
    (let [^OrthographicCamera camera (get-camera viewport)
          plane-points (mapv (fn [^Vector3 v3]
                               [(.x v3) (.y v3) (.z v3)])
                             (.planePoints (.frustum camera)))
          frustum-points (take 4 plane-points)
          left-x   (apply min (map first  frustum-points))
          bottom-y (apply min (map second frustum-points))]
      (draw-fn-grid shape-drawer
                    (int left-x)
                    (int bottom-y)
                    (inc (int (get-world-width viewport)))
                    (+ 2 (int (get-world-height viewport)))
                    1
                    1
                    (float-bits 1 1 1 0.8)))))

(defn highlight-mouseover-tile
  [shape-drawer input viewport grid]
  (let [[x y] (mapv int (unproject viewport [(get-x input) (get-y input)]))
        cell (when-let [cell-atom (get-in grid [x y])]
               @cell-atom)]
    (when (and cell (#{:air :none} (:movement cell)))
      (rectangle! shape-drawer x y 1 1
                         (case (:movement cell)
                           :air (float-bits 1 1 0 0.5)
                           :none (float-bits 1 0 0 0.5))))))

(defn- effect-render
  [[k v] effect-ctx shape-drawer active-entities raycaster]
  (case k
    :effects/target-all
    (let [source (:effect/source effect-ctx)
          source* @source]
      (doseq [target* (map deref (affected-targets active-entities raycaster source*))]
        (line! shape-drawer
                      (:entity/position source*)
                      (:entity/position target*)
                      (float-bits 1 0 0 0.5))))

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
                        (float-bits 1 0 0 0.5)
                        (float-bits 1 1 0 0.5)))))

    nil))

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

(defn player-item-on-cursor-draw
  [v entity batch unit-scale mouseover-actor image->texture-region item-place-position]
  (let [{:keys [item]} v]
    (when-not mouseover-actor
      (draw-texture-region! batch unit-scale
                            (image->texture-region (:entity/image item))
                            (item-place-position (:entity/position entity)
                                                 (- (:entity/click-distance-tiles entity) 0.1))
                            {:center? true}))))

(defn current-frame [{:keys [frames cnt frame-duration]}]
  (frames (min (int (/ (float cnt) (float frame-duration)))
               (dec (count frames)))))

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
                    (float-bits 1 0 0 0.4)
                    (= faction (:entity/faction player))
                    (float-bits 0 1 0 0.4)
                    :else
                    (float-bits 1 1 1 0.4))]
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
        (filled-rectangle! shape-drawer x y width height (float-bits 0 0 0 1))
        (filled-rectangle! shape-drawer
                           (+ x border)
                           (+ y border)
                           (- (* width ratio) (* 2 border))
                           (- height (* 2 border))
                           (cond
                             (> ratio 0.75) (float-bits 0 0.8 0 1)
                             (> ratio 0.5) (float-bits 0 0.5 0 1)
                             (> ratio 0.25) (float-bits 0.5 0.5 0 1)
                             :else (float-bits 0.5 0 0 1)))))))

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
  (filled-circle! shape-drawer (:entity/position entity) 0.5 (float-bits 0.5 0.5 0.5 0.4)))

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
    (filled-circle! shape-drawer center radius (float-bits 1 1 1 0.125))
    (sector! shape-drawer
             center
             radius
             (math/to-radians 90)
             (math/to-radians (* (float action-counter-ratio) 360))
             (float-bits 1 1 1 0.5))
    (draw-texture-region! batch unit-scale (image->texture-region image) [(- (float x) radius) y])
    (doseq [effect effects]
      (effect-render effect effect-ctx shape-drawer
                     active-entities
                     raycaster))))

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
  (circle! shape-drawer (:entity/position entity) 0.5 (float-bits 1 1 1 0.6)))

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
                                (float-bits 1 1 1 1)
                                (float-bits 0.5 0.5 0.5 1))))
    (doseq [[k v] entity
            :when (get render-layer k)]
      (draw-component! k v entity))))

(defn- choose-skill [ray-blocked? entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (effect-applicable? e effect-ctx)))
                          (some (fn [e] (effect-useful? e effect-ctx ray-blocked?))))))
       first))

(defn outer-rectangle
  [{[x y] :position :keys [radius]}]
  (let [radius (float radius)
        size (* radius 2)]
    {:x (- (float x) radius)
     :y (- (float y) radius)
     :width  size
     :height size}))

(defn animation-tick
  [v eid delta-time handle-txs!]
  (let [{:keys [delete-after-stopped?
                looping?
                cnt
                maxcnt]
         :as animation} v]
    (swap! eid assoc :entity/animation (let [maxcnt (float maxcnt)
                                             newcnt (+ (float cnt) (float delta-time))]
                                         (assoc animation :cnt (cond (< newcnt maxcnt) newcnt
                                                                     looping? (min maxcnt (- newcnt maxcnt))
                                                                     :else maxcnt))))
    (when (and delete-after-stopped?
               (and (not looping?) (>= cnt maxcnt)))
      (swap! eid assoc :entity/destroyed? true))
    nil))

(defn get-friendlies-for-alert [eid faction grid]
  (let [circle {:position (:entity/position @eid)
                :radius 4}
        [x y] (:position circle)
        gdx-circle (Circle. (float x) (float y) (float (:radius circle)))]
    (->> circle
         outer-rectangle
         touched-tiles*
         (keep #(get-in grid %))
         (map deref)
         (into #{} (mapcat :entities))
         (filter #(Intersector/overlaps ^Circle gdx-circle
                                        ^Rectangle (rectangle @%)))
         (filter #(= (:entity/faction @%) faction)))))

(defn alert-friendlies-after-duration-tick
  [v eid _delta-time handle-txs! elapsed-time grid]
  (let [{:keys [counter faction]} v]
    (when (timer-stopped? counter elapsed-time)
      (swap! eid assoc :entity/destroyed? true)
      (doseq [friendly-eid (get-friendlies-for-alert eid faction grid)
              ; TODO called on projectiles
              :when (:entity/fsm @friendly-eid)]
        (handle-txs! [[:tx/event friendly-eid :alert]])))
    nil))

(defn string-effect-tick
  [v eid _delta-time _handle-txs! elapsed-time]
  (let [{:keys [counter]} v]
    (when (timer-stopped? counter elapsed-time)
      (swap! eid dissoc :entity/string-effect))
    nil))

(defn skills-tick
  [v eid _delta-time _handle-txs! elapsed-time]
  (doseq [{:keys [skill/cooling-down?] :as skill} (vals v)
          :when (and cooling-down?
                     (timer-stopped? cooling-down? elapsed-time))]
    (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] false))
  nil)

(defn temp-modifier-tick
  [v eid _delta-time _handle-txs! elapsed-time]
  (let [{:keys [modifiers counter]} v]
    (when (timer-stopped? counter elapsed-time)
      (swap! eid dissoc :entity/temp-modifier)
      (swap! eid update :entity/stats remove-mods modifiers))
    nil))

(defn projectile-collision-tick
  [v eid _delta-time handle-txs! grid applicable-effect-txs]
  (let [{:keys [entity-effects already-hit-bodies piercing?]} v
        entity @eid
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
    (when hit-entity
      (swap! eid assoc-in [:entity/projectile-collision :already-hit-bodies]
             (conj already-hit-bodies hit-entity)))
    (when destroy?
      (swap! eid assoc :entity/destroyed? true))
    (when hit-entity
      (handle-txs! (applicable-effect-txs {:effect/source eid 
                                           :effect/target hit-entity} 
                                          entity-effects)))
    nil))

(defn active-skill-tick
  [v eid _delta-time handle-txs! line-of-sight? applicable-effect-txs elapsed-time]
  (let [{:keys [skill effect-ctx counter]} v
        source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)
        effect-ctx (if (and target
                            (not (:entity/destroyed? @target))
                            (line-of-sight? @source @target))
                     effect-ctx
                     (dissoc effect-ctx :effect/target))]
    (cond
      (not (seq (filter #(effect-applicable? % effect-ctx)
                        (:skill/effects skill))))
      (handle-txs! [[:tx/event eid :action-done]])

      (timer-stopped? counter elapsed-time)
      (do (handle-txs! (applicable-effect-txs effect-ctx (:skill/effects skill)))
          (handle-txs! [[:tx/event eid :action-done]])
          nil))))

(defn delete-after-duration-tick
  [v eid _delta-time _handle-txs! elapsed-time]
  (when (timer-stopped? v elapsed-time)
    (swap! eid assoc :entity/destroyed? true))
  nil)

(defn stunned-tick
  [v eid _delta-time handle-txs! elapsed-time]
  (let [{:keys [counter]} v]
    (when (timer-stopped? counter elapsed-time)
      (handle-txs! [[:tx/event eid :effect-wears-off]]))))

(defn npc-moving-tick
  [v eid _delta-time handle-txs! elapsed-time]
  (let [{:keys [timer]} v]
    (when (timer-stopped? timer elapsed-time)
      (handle-txs! [[:tx/event eid :timer-finished]]))))

(defn npc-sleeping-tick
  [_v eid _delta-time handle-txs! grid]
  (let [entity @eid]
    (when-let [distance (nearest-enemy-distance grid entity)]
      (when (<= distance (get-value (:entity/stats entity) :stats/aggro-range))
        (handle-txs! [[:tx/event eid :alert]])))))

(defn npc-idle-tick
  [_v eid _delta-time handle-txs! line-of-sight? raycaster-blocked? grid]
  (let [entity @eid
        target (nearest-enemy grid entity)
        target (when (and target
                          (line-of-sight? entity @target))
                 target)
        effect-ctx {:effect/source eid
                    :effect/target target
                    :effect/target-direction (when target
                                               (direction entity @target))}]
    (if-let [skill (choose-skill raycaster-blocked? entity effect-ctx)]
      (handle-txs! [[:tx/event eid :start-action [skill effect-ctx]]])
      (handle-txs! [[:tx/event eid :movement-direction (or (find-direction grid eid)
                                                           [0 0])]]))))

(defn movement-tick
  [v eid delta-time _handle-txs! grid content-grid max-speed]
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
      (let [movement (assoc movement :delta-time delta-time)
            body @eid]
        (when-let [body (if (:entity/collides? body)
                          (try-move-solid-body grid body (:entity/id @eid) movement)
                          (let [{:keys [direction speed delta-time]} movement]
                            (update body :entity/position
                                    #(mapv (fn [p d] (+ p (* d speed delta-time))) % direction))))]
          (swap! eid assoc :entity/position (:entity/position body))
          (update-entity! content-grid eid)
          (remove-from-touched-cells! grid eid)
          (set-touched-cells! grid eid)
          (when (:entity/collides? @eid)
            (remove-from-occupied-cells! grid eid)
            (set-occupied-cells! grid eid))
          nil)))))

(defn defaults [^Table table]
  (.defaults table))

(defn create-action-bar []
  (let [table (table-create)]
    (pad! (defaults table) 2)
    (doto (add! table (doto (HorizontalGroup.)
                        (.space (float 2))
                        (.pad 2) ; horizontal group pad not 'pad!' from cell
                        (set-name! "moon.ui.action-bar.horizontal-group")
                        (set-user-object! (doto (ButtonGroup.)
                                            (.setMaxCheckCount (int 1))
                                            (.setMinCheckCount (int 0))))))
      (expand!)
      (bottom!))
    (row! table)
    (doto table
      (pack!)
      (set-fill-parent! true)
      (set-name! "moon.ui.action-bar"))))

(defn current-mouseover-actor [stage input]
  (let [[x y] (ui-mouse-position stage input)]
    (mouseover-actor stage x y)))

(defn destroy-entities! [handle-txs! game-db entity-ids grid content-grid]
  (doseq [eid (filter (comp :entity/destroyed? deref) (vals @entity-ids))]
    (let [id (:entity/id @eid)]
      (swap! entity-ids dissoc id)
      (remove-entity! content-grid eid)
      (remove-from-touched-cells! grid eid)
      (when (:entity/collides? @eid)
        (remove-from-occupied-cells! grid eid)))
    (doseq [[k v] @eid]
      (handle-txs!
        (case k
          :entity/destroy-audiovisual
          [[:tx/sound
            (case v
              :audiovisuals/creature-die "bfxr_defaultmonsterdeath.wav"
              :audiovisuals/hit-wall "bfxr_projectile_wallhit.wav")]
           [:tx/spawn-entity (animation-entity game-db (:entity/position @eid) v)]]

          nil)))))

(defn- movement-vector [key-pressed?]
  (let [r (when (key-pressed? Input$Keys/D) [1  0])
        l (when (key-pressed? Input$Keys/A) [-1 0])
        u (when (key-pressed? Input$Keys/W) [0  1])
        d (when (key-pressed? Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v2-normalise (reduce #(mapv + %1 %2) [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2-length v))
          v)))))

(defn- show-message! [stage message]
  (-> (get-root stage)
      (find-actor "player-message")
      (set-user-object! (atom {:text message :counter 0}))))

(defn get-delta-time [graphics]
  (Graphics/.getDeltaTime graphics))

(defn selected-skill [action-bar]
  (let [horizontal-group (find-actor action-bar "moon.ui.action-bar.horizontal-group")
        button-group (get-user-object horizontal-group)]
    (when-let [skill-button (.getChecked ^ButtonGroup button-group)]
      (get-user-object skill-button))))

(defn new-sound [audio file-handle]
  (Audio/.newSound audio file-handle))

(defn clear-screen! [r g b a]
  (ScreenUtils/clear r g b a))

(defn sprite-batch! []
  (SpriteBatch.))

(import '(com.badlogic.gdx.audio Sound))

(defn key-pressed? [input input-key]
  (Input/.isKeyPressed input input-key))

(defn key-just-pressed? [input input-key]
  (Input/.isKeyJustPressed input input-key))

(defn set-projection-matrix! [^Batch batch matrix]
  (.setProjectionMatrix batch matrix))

(defn resize! [^Viewport viewport width height center-camera?]
  (.update viewport width height center-camera?))

(defn act! [^Stage stage]
  (.act stage))

(defn draw! [^Stage stage]
  (.draw stage))

(defn get-frames-per-second [graphics]
  (Graphics/.getFramesPerSecond graphics))

(defn set-cursor! [graphics cursor]
  (Graphics/.setCursor graphics cursor))

(defn set-input-processor! [input processor]
  (.setInputProcessor ^Input input processor))

(require '[malli.error :as me])
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

(require '[reduce-fsm :as fsm])

(defn game-listener 
  [{:keys [game-schemas
           game-db
           info-text
           pausing?
           state->pause-game?
           level-fn
           controls
           world-unit-scale
           slot->y-sprite-idx]
    :as game-config}]
  (let [player-eid (atom nil)
        batch (atom nil)
        cursors (atom nil)
        default-font (atom nil)
        shape-drawer (atom nil)
        shape-drawer-texture (atom nil)
        explored-tile-corners (atom nil)
        tiled-map* (atom nil)
        unit-scale (atom 1)
        paused? (atom false)
        mouseover-eid (atom nil)
        potential-field-cache (atom nil)
        sounds (atom nil)
        stage (atom nil)
        skin (atom nil)
        textures (atom nil)
        image->texture-region #(get-texture-region @textures %)
        viewport (atom nil)
        raycaster (atom nil)
        input (atom nil)
        graphics (atom nil)
        elapsed-time (atom 0)
        id-counter (atom 0)
        entity-ids (atom {})
        grid (atom nil)
        content-grid (atom nil)
        active-entities (atom nil)
        show-potential-field-colors? (atom nil)
        show-cell-entities? (atom false)
        show-cell-occupied? (atom false)
        show-body-bounds? (atom false)
        show-tile-grid? (atom false)
        item-place-position* (fn [position click-distance]
                               (item-place-position (unproject @viewport [(get-x @input) (get-y @input)]) position click-distance))
        set-item! (fn [cell item]
                    (-> (get-root @stage)
                        (find-actor "moon.ui.windows.inventory")
                        (set-item!* cell
                                    {:texture-region (image->texture-region (:entity/image item))
                                     :tooltip-text (item-info-text item)}
                                    @skin)))
        z-orders [:z-order/on-ground
                  :z-order/ground
                  :z-order/flying
                  :z-order/effect]
        minimum-size 0.39
        create-entity-state (fn [[k v] entity]
                              (case k
                                :active-skill
                                (let [[skill effect-ctx] v]
                                  {:skill skill
                                   :effect-ctx effect-ctx
                                   :counter (timer-create (/ (:skill/action-time skill)
                                                             (or (get-value (:entity/stats entity)
                                                                            (:skill/action-time-modifier-key skill))
                                                                 1))
                                                          @elapsed-time)})

                                :stunned
                                {:counter (timer-create v @elapsed-time)}

                                :player-moving
                                {:movement-vector v}

                                :npc-moving
                                {:movement-vector v
                                 :timer (timer-create (* (get-value (:entity/stats entity) :stats/reaction-time)
                                                         0.016)
                                                      @elapsed-time)}

                                :player-item-on-cursor
                                {:item v}

                                v))
        after-create-component (fn [eid [k v]]
                                 (case k
                                   :entity/fsm
                                   (let [{:keys [fsm initial-state]} v]
                                     (swap! eid assoc :entity/fsm (create-fsm fsm initial-state))
                                     (swap! eid assoc initial-state (create-entity-state [initial-state nil] @eid))
                                     nil)

                                   :entity/skills
                                   (do
                                     (swap! eid assoc :entity/skills nil)
                                     (doseq [{:keys [property/id] :as skill} v]
                                       (assert (not (contains? (:entity/skills @eid) id)))
                                       (swap! eid update :entity/skills assoc id skill))
                                     nil)

                                   :entity/inventory
                                   (do
                                     (swap! eid assoc :entity/inventory (->> #:inventory.slot{:bag      [6 4]
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
                                     (doseq [item v]
                                       (assert (valid-item? item))
                                       (let [[cell cell-item] (can-pickup-item? (:entity/inventory @eid) item)]
                                         (assert cell)
                                         (assert (nil? cell-item))
                                         (swap! eid set-item cell item)))
                                     nil)

                                   nil))
        construct-entity (fn [entity]
                           (let [entity (reduce (fn [m [k v]]
                                                  (assoc m k (case k
                                                               :entity/animation
                                                               (create-animation v)

                                                               :entity/delete-after-duration
                                                               (timer-create v @elapsed-time)

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
        spawn-entity! (fn [entity]
                        (let [eid (atom (construct-entity entity))]
                          (assert (and (not (contains? @eid :entity/id))))
                          (let [id (swap! id-counter inc)]
                            (assert (number? id))
                            (swap! eid assoc :entity/id id)
                            (swap! entity-ids assoc id eid))
                          (assert (:entity/position @eid))
                          (update-entity! @content-grid eid)
                          (assert (:entity/position @eid))
                          (when (:entity/collides? @eid)
                            (assert (valid-position? @grid @eid (:entity/id @eid))))
                          (set-touched-cells! @grid eid)
                          (when (:entity/collides? @eid)
                            (set-occupied-cells! @grid eid))
                          (doseq [component @eid]
                            (after-create-component eid component))
                          eid))
        entity-state-enter! (fn [eid [state-k state-v]]
                              (case state-k
                                :player-item-on-cursor
                                (let [{:keys [item]} state-v]
                                  [[:tx/swap eid assoc :entity/item-on-cursor item]])

                                :active-skill
                                (let [{:keys [skill]} state-v
                                      cost (:skill/cost skill)]
                                  [[:tx/swap eid update :entity/stats (fn [stats]
                                                                        (let [mana-val ((get-mana stats) 0)]
                                                                          (assert (<= cost mana-val))
                                                                          (assoc-in stats [:stats/mana 0] (- mana-val cost))))]
                                   [:tx/swap eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] (timer-create (:skill/cooldown skill) @elapsed-time)]
                                   [:tx/sound (:skill/start-action-sound skill)]])

                                :npc-dead
                                [[:tx/swap eid assoc :entity/destroyed? true]]

                                :player-moving
                                (let [{:keys [movement-vector]} state-v]
                                  [[:tx/swap eid assoc :entity/movement {:direction movement-vector
                                                                         :speed (or (get-value (:entity/stats @eid) :stats/movement-speed)
                                                                                    0)}]])

                                :player-dead
                                [
                                 [:tx/sound "bfxr_playerdeath.wav"]
                                 [:tx/modal {:title "YOU DIED - again!"
                                             :text "Good luck next time!"
                                             :button-text "OK"
                                             :on-click (fn [])}]
                                 ]

                                :npc-moving
                                (let [{:keys [movement-vector]} state-v]
                                  [[:tx/swap eid assoc :entity/movement {:direction movement-vector
                                                                         :speed (or (get-value (:entity/stats @eid) :stats/movement-speed)
                                                                                    0)}]])

                                nil))
        entity-state-exit! (fn [eid [state-k _state-v]]
                             (case state-k
                               :player-item-on-cursor
                               (let [entity @eid
                                     item (:entity/item-on-cursor entity)]
                                 (when item
                                   [[:tx/swap eid dissoc :entity/item-on-cursor]
                                    [:tx/sound "bfxr_itemputground.wav"]
                                    [:tx/spawn-entity (spawn-item (item-place-position* (:entity/position entity)
                                                                                        (- (:entity/click-distance-tiles entity) 0.1))
                                                                  item)]]))

                               :player-moving
                               [[:tx/swap eid dissoc :entity/movement]]

                               :npc-sleeping
                               [[:tx/swap eid add-text-effect "[WHITE]!" 1 #(timer-create % @elapsed-time)]
                                [:tx/spawn-entity (spawn-alert (:entity/position @eid) (:entity/faction @eid) 0.2 @elapsed-time)]]

                               :npc-moving
                               [[:tx/swap eid dissoc :entity/movement]]

                               nil))
        fsm-event-txs (fn [eid event & [params]]
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
                                      (entity-state-exit! eid old-state-obj)
                                      (entity-state-enter! eid new-state-obj))))))
        handle-txs! (fn handle-txs! [txs]
                      (let [k->txs {
                                    :tx/swap 
                                    (fn [eid f & params]
                                      (apply swap! eid f params))
                                    :tx/sound 
                                    (fn [sound-name]
                                      (Sound/.play (safe-get @sounds sound-name)))
                                    :tx/spawn-entity spawn-entity!
                                    :tx/event
                                    ; cant remove this because item on cursor triggers to :tx/event after another
                                    ; which are order dependent!
                                    ; at player-item-on-cursor inventory/world/ui magic.
                                    (fn [& params]
                                      (handle-txs! (apply fsm-event-txs params)))
                                    :tx/modal
                                    (fn [opts]
                                      (show-modal! @skin @stage opts))
                                    :tx/set-item set-item!
                                    :tx/remove-item
                                    (fn [cell] 
                                      (remove-item! @stage cell))
                                    :tx/toggle-inventory-visible
                                    (fn []
                                      (toggle-inventory-visible! @stage))
                                    :tx/show-message
                                    (fn [message]
                                      (show-message! @stage message))
                                    }]
                        (doseq [[k & params] txs]
                          (try (apply (safe-get k->txs k) params)
                               (catch Throwable t
                                 (throw (ex-info "handle-txs! error" 
                                                 {:k k :params params}
                                                 t)))))))
        set-skill! (fn [skill]
                     (-> (get-root @stage)
                         (find-actor "moon.ui.action-bar")
                         (add-skill! {:skill-id (:property/id skill)
                                      :texture-region (image->texture-region (:entity/image skill))
                                      :tooltip-text (info-text skill @elapsed-time)}
                                     @skin)))
        sync-player-ui! (fn []
                          (doseq [skill (vals (:entity/skills @@player-eid))]
                            (set-skill! skill))
                          (doseq [[slot grid] (:entity/inventory @@player-eid)
                                  x (range (count grid))
                                  y (range (count (grid 0)))
                                  :let [position [x y]
                                        item (get-in grid position)]
                                  :when item]
                            (set-item! [slot position] item)))
        controls-info (str/join "\n"
                                ["[W][A][S][D] - Move"
                                 "[ESCAPE] - Close windows"
                                 "[I] - Inventory window"
                                 "[E] - Entity Info window"
                                 "[-]/[=] - Zoom"
                                 "[P]/[SPACE] - Unpause"
                                 "Leftmouse click - use skill/drop item on cursor"])
        menus [{:label "Debug"
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
                :items [{:label controls-info}]}]
        update-labels (for [item [{:label "elapsed-time"
                                   :update-fn (fn [] 
                                                (str (readable @elapsed-time) " seconds"))
                                   :icon "images/clock.png"}
                                  {:label "FPS"
                                   :update-fn (fn [] (get-frames-per-second @graphics))
                                   :icon "images/fps.png"}
                                  {:label "Mouseover-entity id"
                                   :update-fn (fn []
                                                (when-let [entity (and @mouseover-eid @@mouseover-eid)]
                                                  (:entity/id entity)))
                                   :icon "images/mouseover.png"}
                                  {:label "paused?"
                                   :update-fn (fn [] @paused?)}
                                  {:label "GUI"
                                   :update-fn (fn []
                                                (mapv int (ui-mouse-position @stage @input)))}
                                  {:label "World"
                                   :update-fn (fn []
                                                (mapv int (unproject @viewport [(get-x @input) (get-y @input)])))}
                                  {:label "Zoom"
                                   :update-fn (fn []
                                                (.zoom ^OrthographicCamera (get-camera @viewport)))
                                   :icon "images/zoom.png"}]]
                        (if (:icon item)
                          (update item :icon #(get @textures %))
                          item))
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
                          :pickup-item -> :player-item-on-cursor
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
                         [:player-item-on-cursor
                          :kill -> :player-dead
                          :stun -> :stunned
                          :drop-item -> :player-idle
                          :dropped-item -> :player-idle]
                         [:player-dead]])}
        mk-effect-txs (fn applicable-effect-txs [effect-ctx effects]
                        (mapcat (fn [[k v]] 
                                  (case k
                                    :effects/projectile (projectile-effect v effect-ctx)
                                    :effects/spawn (spawn-effect* v effect-ctx game-db fsms)
                                    :effects/target-all (target-all-effect v effect-ctx @active-entities @raycaster applicable-effect-txs)
                                    :effects/target-entity (target-entity-effect v effect-ctx game-db applicable-effect-txs)
                                    :effects.target/convert (convert-effect v effect-ctx game-db)
                                    :effects.target/damage (damage-effect v effect-ctx game-db @elapsed-time)
                                    :effects.target/kill (kill-effect v effect-ctx)
                                    :effects.target/melee-damage (damage-effect (melee-damage @(:effect/source effect-ctx)) effect-ctx game-db @elapsed-time)
                                    :effects.target/spiderweb (spiderweb-effect v effect-ctx @elapsed-time)
                                    :effects.target/stun (stun-effect v effect-ctx)))
                                (filter #(effect-applicable? % effect-ctx) effects)))
        tick-component (fn [[k v] eid delta-time elapsed-time]
                         (case k
                           :entity/animation
                           (animation-tick v eid delta-time handle-txs!)

                           :entity/alert-friendlies-after-duration
                           (alert-friendlies-after-duration-tick v eid delta-time handle-txs! elapsed-time @grid)

                           :entity/string-effect
                           (string-effect-tick v eid delta-time handle-txs! elapsed-time)

                           :entity/skills
                           (skills-tick v eid delta-time handle-txs! elapsed-time)

                           :entity/temp-modifier
                           (temp-modifier-tick v eid delta-time handle-txs! elapsed-time)

                           :entity/projectile-collision
                           (projectile-collision-tick v eid delta-time handle-txs! @grid mk-effect-txs)

                           :active-skill
                           (active-skill-tick v eid delta-time handle-txs! (partial line-of-sight? @raycaster) mk-effect-txs elapsed-time)

                           :entity/delete-after-duration
                           (delete-after-duration-tick v eid delta-time handle-txs! elapsed-time)

                           :stunned
                           (stunned-tick v eid delta-time handle-txs! elapsed-time)

                           :npc-moving
                           (npc-moving-tick v eid delta-time handle-txs! elapsed-time)

                           :npc-sleeping
                           (npc-sleeping-tick v eid delta-time handle-txs! @grid)

                           :npc-idle
                           (npc-idle-tick v eid delta-time handle-txs!
                                          (partial line-of-sight? @raycaster)
                                          (partial raycaster-blocked? @raycaster)
                                          @grid)

                           :entity/movement
                           (movement-tick v eid delta-time handle-txs! @grid @content-grid max-speed)

                           nil))
        tick-entities! (fn [delta-time elapsed-time]
                         (doseq [eid @active-entities
                                 component @eid]
                           (tick-component component eid delta-time elapsed-time)))
        factions-iterations {:good 15 :evil 5}
        tick-game! (fn [delta-time]
                     (when-not @paused?
                       (let [delta-time (min delta-time max-delta)]
                         (swap! elapsed-time + delta-time)
                         (doseq [[faction max-iterations] factions-iterations]
                           (update! @grid
                                    potential-field-cache
                                    faction
                                    @active-entities
                                    max-iterations))
                         (tick-entities! delta-time @elapsed-time))))
        create-level! (fn []
                        (let [{level-tiled-map :tiled-map
                               level-start :start-position}
                              (level-fn {:level/creature-properties (prepare-creature-tiles
                                                                      (all-raw game-db :properties/creatures)
                                                                      image->texture-region)
                                         :textures @textures})]
                          {:tiled-map level-tiled-map
                           :start-position level-start}))
        create-interaction-state (fn [input]
                                   (let [player-eid @player-eid
                                         stage @stage
                                         mouseover-eid @mouseover-eid
                                         mouseover-actor (current-mouseover-actor stage input)]
                                     (cond
                                       mouseover-actor
                                       [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor)]

                                       (and mouseover-eid
                                            (:entity/clickable @mouseover-eid))
                                       [:interaction-state/clickable-mouseover-eid
                                        {:clicked-eid mouseover-eid
                                         :in-click-range? (< (v2-distance (:entity/position @player-eid)
                                                                          (:entity/position @mouseover-eid))
                                                             (:entity/click-distance-tiles @player-eid))}]

                                       :else
                                       (if-let [skill-id (-> (get-root stage)
                                                             (find-actor "moon.ui.action-bar")
                                                             selected-skill)]
                                         (let [entity @player-eid
                                               skill (skill-id (:entity/skills entity))
                                               target-position (or (and mouseover-eid
                                                                        (:entity/position @mouseover-eid))
                                                                   (unproject @viewport [(get-x input) (get-y input)]))
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
        interaction-state->txs (fn [[k params] stage player-eid]
                                 (case k
                                   :interaction-state/mouseover-actor
                                   nil

                                   :interaction-state/clickable-mouseover-eid
                                   (let [{:keys [clicked-eid in-click-range?]} params]
                                     (if in-click-range?
                                       (case (:type (:entity/clickable @clicked-eid))
                                         :clickable/player
                                         [[:tx/toggle-inventory-visible]]

                                         :clickable/item
                                         (let [item (:entity/item @clicked-eid)]
                                           (cond
                                             (-> (get-root stage)
                                                 (find-actor "moon.ui.windows.inventory")
                                                 visible?)
                                             [[:tx/swap clicked-eid assoc :entity/destroyed? true]
                                              [:tx/sound "bfxr_takeit.wav"]
                                              [:tx/event player-eid :pickup-item item]]

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
                                     [[:tx/event player-eid :start-action [skill effect-ctx]]])

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
        cursor-key (fn [player-eid interaction-state]
                     (let [eid player-eid
                           entity @eid
                           state-k (:state (:entity/fsm entity))
                           cursor-fn (get {:player-item-on-cursor :cursors/hand-grab
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
        handle-player-input (fn [player-eid interaction-state stage input key-pressed? button-just-pressed?]
                              (case (:state (:entity/fsm @player-eid))
                                :player-idle
                                (if-let [move-v (movement-vector key-pressed?)]
                                  [[:tx/event player-eid :movement-input move-v]]
                                  (when (button-just-pressed? Input$Buttons/LEFT)
                                    (interaction-state->txs interaction-state stage player-eid)))

                                :player-moving
                                (if-let [move-v (movement-vector key-pressed?)]
                                  [[:tx/swap player-eid assoc :entity/movement {:direction move-v
                                                                                :speed (or (get-value (:entity/stats @player-eid) :stats/movement-speed)
                                                                                           0)}]]
                                  [[:tx/event player-eid :no-movement-input]])

                                :player-item-on-cursor
                                (let [[x y] (ui-mouse-position stage input)]
                                  (when (and (button-just-pressed? Input$Buttons/LEFT)
                                             (not (mouseover-actor stage x y)))
                                    [[:tx/event player-eid :drop-item]]))

                                nil))
        mouse-interaction! (fn []
                             (let [interaction-state (create-interaction-state @input)]
                               (set-cursor! @graphics (safe-get @cursors (cursor-key @player-eid interaction-state)))
                               (handle-txs! (handle-player-input @player-eid
                                                                 interaction-state
                                                                 @stage
                                                                 @input
                                                                 #(key-pressed? @input (int %))
                                                                 #(.isButtonJustPressed ^Input @input (int %))))))
        update-paused-state! (fn []
                               (reset! paused?
                                       (or #_error
                                           (and pausing?
                                                (state->pause-game? (:state (:entity/fsm @@player-eid)))
                                                (not (or (key-just-pressed? @input (int (:unpause-once controls)))
                                                         (key-pressed? @input (int (:unpause-continously controls)))))))))
        remove-destroyed-entities! (fn [] (destroy-entities! handle-txs! game-db entity-ids @grid @content-grid))
        key-pressed-checks! (fn []
                              (doseq [[k f] {(:zoom-in controls) (fn []
                                                                   (let [^OrthographicCamera camera (get-camera @viewport)]
                                                                     (set! (.zoom camera) (max 0.1 (+ (.zoom camera) 0.025)))
                                                                     (.update camera)))
                                             (:zoom-out controls) (fn []
                                                                    (let [^OrthographicCamera camera (get-camera @viewport)]
                                                                      (set! (.zoom camera) (max 0.1 (+ (.zoom camera) -0.025)))
                                                                      (.update camera)))}]
                                (when (key-pressed? @input (int k))
                                  (f))))
        key-just-pressed-checks! (fn []
                                   (doseq [[k f] {(:close-windows-key controls) (fn []
                                                                                  (close-windows! @stage))
                                                  (:toggle-inventory controls) (fn []
                                                                                 (toggle-inventory-visible! @stage))
                                                  (:toggle-entity-info controls) (fn []
                                                                                   (toggle-entity-info! @stage))}]
                                     (when (key-just-pressed? @input (int k))
                                       (f))))
        update-stage! #(act! @stage)
        draw-stage! #(draw! @stage)
        resize-stage! (fn [width height]
                        (resize! (get-viewport @stage) width height true))
        resize-viewport! (fn [width height]
                           (resize! @viewport width height false))
        batch-end! #(.end ^Batch @batch)
        get-mouseover-actor #(let [[x y] (ui-mouse-position @stage @input)]
                               (mouseover-actor @stage x y))
        active-skill-radius (let [tile-size 48
                                  image-width 32]
                              (/ (/ image-width tile-size) 2))
        draw-component! (fn [k v entity]
                          (case k
                            :entity/clickable
                            (clickable-draw v entity @batch @default-font @unit-scale)
                            :player-item-on-cursor
                            (player-item-on-cursor-draw v entity @batch @unit-scale (get-mouseover-actor) image->texture-region item-place-position*)
                            :entity/animation
                            (animation-draw v entity @batch @unit-scale image->texture-region)
                            :entity/image
                            (image-draw v entity @batch @unit-scale image->texture-region)
                            :entity/line-render
                            (line-render-draw v entity @shape-drawer)
                            :entity/mouseover?
                            (mouseover?-draw entity @shape-drawer @@player-eid)
                            :entity/stats
                            (stats-draw entity @shape-drawer @unit-scale)
                            :entity/string-effect
                            (string-effect-draw v entity @batch @default-font @unit-scale)
                            :entity/temp-modifier
                            (temp-modifier-draw entity @shape-drawer)
                            :active-skill
                            (active-skill-draw v entity @shape-drawer @batch @unit-scale image->texture-region @active-entities @raycaster @elapsed-time active-skill-radius)
                            :npc-sleeping
                            (npc-sleeping-draw entity @batch @default-font @unit-scale)
                            :stunned
                            (stunned-draw entity @shape-drawer)))
        render-z-order (apply hash-map (interleave z-orders (range)))
        render-layers [#{:entity/mouseover?
                         :stunned
                         :player-item-on-cursor}
                       #{:entity/clickable
                         :entity/animation
                         :entity/image
                         :entity/line-render}
                       #{:npc-sleeping
                         :entity/temp-modifier
                         :entity/string-effect}
                       #{:entity/stats
                         :active-skill}]
        draw-world-layer! #(with-line-width @shape-drawer world-unit-scale
                             (reset! unit-scale world-unit-scale)
                             (draw-tile-grid @shape-drawer @viewport @show-tile-grid?)
                             (draw-cell-debug factions-iterations
                                              @shape-drawer @viewport
                                              @show-cell-entities?
                                              @show-cell-occupied?
                                              @show-potential-field-colors?
                                              @grid)
                             (draw-entities! draw-component!
                                             @shape-drawer
                                             @raycaster
                                             @active-entities
                                             @@player-eid
                                             @show-body-bounds?
                                             render-z-order
                                             render-layers)
                             (highlight-mouseover-tile @shape-drawer @input @viewport @grid)
                             (reset! unit-scale 1))
        set-batch-color! #(.setColor ^Batch @batch (float 1) (float 1) (float 1) (float 1))
        render-tiled-map! #(draw-tiled-map! @tiled-map*
                                            @batch
                                            (get-camera @viewport)
                                            (tile-color-setter*
                                              {:ray-blocked? (partial raycaster-blocked? @raycaster)
                                               :explored-tile-corners explored-tile-corners
                                               :light-position (let [pos (.position ^OrthographicCamera (get-camera @viewport))]
                                                                 [(.x pos) (.y pos) (.z pos)])
                                               :see-all-tiles? false
                                               :explored-tile-color (float-bits 0.5 0.5 0.5 1)
                                               :visible-tile-color (float-bits 1 1 1 1)
                                               :invisible-tile-color (float-bits 0 0 0 1)})
                                            world-unit-scale)
        batch-begin! #(.begin ^Batch @batch)
        set-proj-matrix! #(set-projection-matrix! @batch (.combined ^OrthographicCamera (get-camera @viewport)))
        set-camera-to-player! (fn []
                                (let [^OrthographicCamera camera (get-camera @viewport)
                                      pos (.position camera)
                                      [x y] (:entity/position @@player-eid)]
                                  (set! (.x pos) x)
                                  (set! (.y pos) y)
                                  (.update camera)))
         update-active-entities! (fn []
                                   (reset! active-entities (active-entities* @content-grid @@player-eid)))
         update-mouseover-eid! (fn []
                                 (let [old-mouseover-eid @mouseover-eid
                                       [x y] (ui-mouse-position @stage @input)
                                       new-eid (if (mouseover-actor @stage x y)
                                                 nil
                                                 (let [player @@player-eid
                                                       hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                                                    (point->entities @grid (unproject @viewport [(get-x @input) (get-y @input)])))]
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
        slot->texture-region (fn [slot]
                               (let [width 48
                                     height 48
                                     sprite-x 21
                                     sprite-y (+ (slot->y-sprite-idx slot) 2)
                                     bounds [(* sprite-x width)
                                             (* sprite-y height)
                                             width
                                             height]]
                                 (image->texture-region {:image/file "images/items.png"
                                                         :image/bounds bounds})))
        ]
    {
     :create!
     (fn [{:keys [audio files] :as capabilities}]
       (reset! graphics (:graphics capabilities))
       (reset! input (:input capabilities))
       (reset! cursors (update-vals (:cursors game-config)
                                    (fn [[path [hotspot-x hotspot-y]]]
                                      (let [pixmap (pixmap (internal files path))
                                            cursor (new-cursor (:graphics capabilities) pixmap hotspot-x hotspot-y)]
                                        (dispose! pixmap)
                                        cursor))))
       (reset! sounds (into {}
                            (for [path (:sounds game-config)]
                              [path (new-sound audio (internal files path))])))
       (reset! batch (sprite-batch!))
       (reset! default-font (create-default-font! files 
                                                  {:path "fonts/films.EXL_____.ttf"
                                                   :size 16
                                                   :quality-scaling 2
                                                   :use-integer-positions? false}))
       (reset! shape-drawer-texture (white-pixel-texture))
       (reset! shape-drawer (ShapeDrawer. @batch (TextureRegion. ^Texture @shape-drawer-texture 1 0 1 1)))
       (reset! skin (create-skin! (internal files (:skin game-config))))
       (reset! stage (Stage. (FitViewport. (float (:width (:stage game-config))) 
                                           (float (:height (:stage game-config)))) 
                             @batch))
       (set-input-processor! (:input capabilities) @stage)
       (set! (.initialTime (TooltipManager/getInstance)) 0)
       (Colors/put "PRETTY_NAME" (Color. 0.84 0.8 0.52 1))
       (reset! textures (create-textures files (:textures game-config)))
       (reset! viewport (create-viewport! 1440 900 world-unit-scale))
       (let [{:keys [^TiledMap tiled-map 
                     start-position]} (create-level!)]
         (reset! tiled-map* tiled-map)
         (reset! id-counter 0)
         (reset! entity-ids {})
         (let [props (get-properties tiled-map)
               width (props-get props "width")
               height (props-get props "height")
               g (create-grid tiled-map)]
           (reset! grid g) 
           (reset! content-grid (create-content-grid width height 16))
           (reset! explored-tile-corners (mapv (fn [x] (mapv (fn [y] false)
                                                             (range height)))
                                               (range width)))
           (reset! raycaster (create-raycaster g)))
         (reset! elapsed-time 0)
         (let [eid (spawn-entity! (spawn-creature {:position (mapv (partial + 0.5) start-position)
                                                   :creature-property (build game-schemas game-db :creatures/vampire)
                                                   :components {:entity/fsm {:fsm (:player fsms)
                                                                             :initial-state :player-idle}
                                                                :entity/faction :good
                                                                :entity/player? true
                                                                :entity/free-skill-points 3
                                                                :entity/clickable {:type :clickable/player}
                                                                :entity/click-distance-tiles 1.5}}))]
           (let [eid (get @entity-ids 1)]
             (assert (:entity/player? @eid))
             (reset! player-eid eid)))
         (doseq [actor [(create-action-bar)
                        (create-dev-menu {:menus menus
                                          :update-labels update-labels
                                          :skin @skin})
                        (create-actor
                          (let [y-mana 80
                                rahmenw 150
                                rahmenh 26
                                x (/ (get-world-width (get-viewport @stage)) 2)
                                rahmen-file "images/rahmen.png"
                                hpcontent-file "images/hp.png"
                                manacontent-file "images/mana.png"
                                y-hp (+ y-mana rahmenh)
                                bar-x (- x (/ rahmenw 2))
                                draw-bar! (fn [batch x y content-file minmaxval name]
                                            (draw-texture-region! batch
                                                                  @unit-scale
                                                                  (image->texture-region {:image/file rahmen-file})
                                                                  [x y])
                                            (draw-texture-region! batch
                                                                  @unit-scale
                                                                  (image->texture-region
                                                                    {:image/file content-file
                                                                     :image/bounds [0 0 (* rahmenw (val-max-ratio minmaxval)) rahmenh]})
                                                                  [x y])
                                            (draw-text! batch 
                                                        @default-font 
                                                        @unit-scale 
                                                        {:text (str (readable (minmaxval 0))
                                                                    "/"
                                                                    (minmaxval 1)
                                                                    " "
                                                                    name)
                                                         :x (+ x 75)
                                                         :y (+ y 2)
                                                         :up? true}))]
                            {:draw! 
                             (fn [batch _parent-alpha]
                               (draw-bar! batch bar-x y-hp hpcontent-file (get-hitpoints (:entity/stats @@player-eid)) "HP")
                               (draw-bar! batch bar-x y-mana manacontent-file (get-mana (:entity/stats @@player-eid)) "MP"))}))
                        (windows-create [(create-info-window
                                           {:title "Entity Info"
                                            :actor-name "moon.ui.windows.entity-info"
                                            :visible? false
                                            :position [(get-world-width (get-viewport @stage)) 0]
                                            :set-label-text! (fn []
                                                               (if-let [eid @mouseover-eid]
                                                                 (info-text (apply dissoc @eid [:entity/skills
                                                                                                :entity/faction
                                                                                                :active-skill])
                                                                            @elapsed-time)
                                                                 ""))
                                            :skin @skin})
                                         (let [cell-size 48]
                                           (create-inventory-window
                                             slot->texture-region
                                             @player-eid
                                             (:input capabilities)
                                             (fn [_event cell]
                                               (handle-txs! (handle-clicked-inventory-cell @player-eid cell)))
                                             (fn [player-entity x y mouseover? cell]
                                               (rectangle! @shape-drawer x y cell-size cell-size (float-bits 0.5 0.5 0.5 1))
                                               (when (and mouseover?
                                                          (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                                                 (let [item (:entity/item-on-cursor player-entity)
                                                       color (if (valid-slot? cell item)
                                                               (float-bits 0 0.6 0 0.8)
                                                               (float-bits 0.6 0 0 0.8))]
                                                   (filled-rectangle! @shape-drawer (inc x) (inc y) (- cell-size 2) (- cell-size 2) color))))
                                             @skin
                                             @stage))])
                        (player-state-draw-create @player-eid unit-scale image->texture-region (:input capabilities))
                        (player-message-actor-create @default-font unit-scale)]]
           (add-actor! @stage actor))
         (sync-player-ui!)
         (handle-txs! (spawn-map-creatures game-schemas game-db @tiled-map* start-position fsms))))
     :dispose!
     (fn []
       (run! dispose! (vals @sounds))
       (dispose! @batch)
       (run! dispose! (vals @cursors))
       (dispose! @default-font)
       (dispose! @shape-drawer-texture)
       (dispose! @skin)
       (run! dispose! (vals @textures))
       (dispose! @tiled-map*))

     :render!
     (fn [capabilities]
       (clear-screen! 0 0 0 0)
       (update-mouseover-eid!)
       (update-active-entities!)
       (set-camera-to-player!)
       (set-proj-matrix!)
       (batch-begin!)
       (render-tiled-map!)
       (set-batch-color!)
       (draw-world-layer!)
       (batch-end!)
       (mouse-interaction!)
       (update-paused-state!)
       (tick-game! (get-delta-time (:graphics capabilities)))
       (remove-destroyed-entities!)
       (key-pressed-checks!)
       (key-just-pressed-checks!)
       (update-stage!)
       (draw-stage!))

     :resize!
     (fn [width height]
       (resize-stage! width height)
       (resize-viewport! width height))
     }))

(defn- text-button [skin label on-click!]
  (doto (create-text-button label skin)
    (add-listener! (create-change-listener
                     (fn [_event _actor]
                       (on-click!))))))

(require '[clojure.edn :as edn])
(require '[clojure.java.io :as io])
(defn levelgen-listener []
  (let [world-unit-scale (/ 48)
        world-width (* 1440 world-unit-scale)
        world-height (* 900 world-unit-scale)
        batch (atom nil)
        skin (atom nil)
        stage (atom nil)
        viewport (atom nil)
        camera (atom nil)
        db (atom nil)
        textures (atom nil)
        image->texture-region #(get-texture-region @textures %)
        tiled-map (atom nil)
        generate-level! (fn [level-fn]
                          (let [level (level-fn {:level/creature-properties
                                                 (prepare-creature-tiles
                                                   (all-raw @db :properties/creatures)
                                                   image->texture-region)
                                                 :textures @textures})
                                ^TiledMap tiled-map (:tiled-map level)
                                props (get-properties tiled-map)
                                width (props-get props "width")
                                height (props-get props "height")]
                            (assert tiled-map)
                            (set-visible! (layers-get (get-layers tiled-map) "creatures") true)
                            (let [^OrthographicCamera camera @camera
                                  pos (.position camera)
                                  px (float (/ width 2))
                                  py (float (/ height 2))
                                  x-diff (max (- px (float 0)) (- (float width) px))
                                  y-diff (max (- (float height) py) (- py (float 0)))
                                  vp-ratio-w (/ (* x-diff 2) (.viewportWidth camera))
                                  vp-ratio-h (/ (* y-diff 2) (.viewportHeight camera))]
                              (set! (.x pos) px)
                              (set! (.y pos) py)
                              (set! (.zoom camera) (max vp-ratio-w vp-ratio-h))
                              (.update camera))
                            tiled-map))
        buttons (for [[label level-fn] [["Vampire" create-vampire-level]
                                        ["UF Caves" create-uf-caves]
                                        ["Modules" create-modules-level]]]
                  [(str "Generate " label)
                   (fn []
                     (dispose! @tiled-map)
                     (reset! tiled-map (generate-level! level-fn)))])]
    {:create!
     (fn [{:keys [files input]}]
       (reset! batch (sprite-batch!))
       (reset! skin (create-skin (internal files "skin/uiskin.json")))
       (reset! stage (let [stage (Stage. (FitViewport. 1440 900) @batch)
                           window (create-window "Edit" @skin)] 
                       (doseq [[label on-click!] buttons]
                         (add! window (text-button @skin label on-click!))
                         (row! window))
                       (pack! window)
                       (add-actor! stage window)
                       stage))
       (set-input-processor! input @stage) 
       (reset! viewport (FitViewport. (float world-width)
                                      (float world-height)
                                      (doto (OrthographicCamera.)
                                        (.setToOrtho false
                                                     world-width
                                                     world-height))))
       (reset! camera (get-camera @viewport)) 
       (reset! db (create-db (-> "schema.edn" io/resource slurp edn/read-string) 
                             (-> "properties.edn" io/resource slurp edn/read-string)))
       (reset! textures (create-textures files
                                         {:folder "resources/"
                                          :extensions #{"png" "bmp"}}))
       (reset! tiled-map (generate-level! create-uf-caves))) 

     :dispose!
     (fn []
       (dispose! @batch)
       (dispose! @skin)
       (run! dispose! (vals @textures))
       (dispose! @tiled-map))

     :render!
     (fn [{:keys [input]}]
       (clear-screen! 0 0 0 0) 
       (set-projection-matrix! @batch (.combined ^OrthographicCamera (get-camera @viewport)))
       (Batch/.begin @batch) 
       (draw-tiled-map! @tiled-map
                        @batch
                        (get-camera @viewport)
                        (constantly (.toFloatBits Color/WHITE))
                        world-unit-scale)
       (Batch/.end @batch)
       (doseq [[k f] {Input$Keys/MINUS (fn [^OrthographicCamera camera] 
                                         (set! (.zoom camera) (max 0.1 (+ (.zoom camera) 0.1)))
                                         (.update camera))
                      Input$Keys/EQUALS (fn [^OrthographicCamera camera]
                                          (set! (.zoom camera) (max 0.1 (+ (.zoom camera) -0.1)))
                                          (.update camera))
                      Input$Keys/LEFT (fn [^OrthographicCamera camera]
                                        (let [pos (.position camera)]
                                          (set! (.x pos) (- (.x pos) 1))
                                          (.update camera)))
                      Input$Keys/RIGHT (fn [^OrthographicCamera camera]
                                         (let [pos (.position camera)]
                                           (set! (.x pos) (+ (.x pos) 1))
                                           (.update camera)))
                      Input$Keys/UP (fn [^OrthographicCamera camera]
                                      (let [pos (.position camera)]
                                        (set! (.y pos) (+ (.y pos) 1))
                                        (.update camera)))
                      Input$Keys/DOWN (fn [^OrthographicCamera camera]
                                        (let [pos (.position camera)]
                                          (set! (.y pos) (- (.y pos) 1))
                                          (.update camera)))}]
         (when (key-pressed? input k)
           (f @camera)))
       (act! @stage)
       (draw! @stage))

     :resize!
     (fn [width height]
       (resize! (get-viewport @stage) width height true)
       (resize! @viewport width height false))
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
        (let [stack (create-stack)
              texture-region (get-texture-region (or (:entity/image property)
                                                     (first (:animation/frames (:entity/animation property)))))]
          (run! #(add-actor! stack %)
                [(doto (ImageButton.
                         (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                           (.setMinSize (* image-scale (.getRegionWidth ^TextureRegion texture-region))
                                        (* image-scale (.getRegionHeight ^TextureRegion texture-region)))))
                   (add-listener! (create-change-listener
                                    (fn [event actor]
                                      (clicked-id-fn actor (:property/id property)))))
                   (add-listener! (text-tooltip (binding [*print-level* 2]
                                                  (with-out-str
                                                    (pprint/pprint property))))))
                 (doto (new-label (extra-info-text property))
                   (set-touchable! Touchable/disabled))])
          stack)))))

(defn property-overview-window [property-type clicked-id-fn {:keys [editor/new-window
                                                                    editor/new-text-button] :as editor}]
  (let [window (new-window "Edit")]
    (doseq [row (property-overview-rows property-type clicked-id-fn editor)]
      (doseq [actor row]
        (add! window actor))
      (row! window))
    (pack! window)
    (add! (get-title-table window)
          (doto (new-text-button "X")
            (add-listener! (create-change-listener
                             (fn [_event _actor]
                               (remove! window))))))
    (set-modal! window true)
    window))

(defn scaled-image-button [texture-region scale]
  (ImageButton.
   (doto (TextureRegionDrawable. ^TextureRegion texture-region)
     (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                  (* scale (.getRegionHeight ^TextureRegion texture-region))))))

(defn create-image-widget [image get-texture-region]
  (scaled-image-button (get-texture-region image) 2))

(defn create-animation-widget 
  [{:keys [pad columns]}]
  (let [table (table-create)]
    (pad! (defaults table) pad)
    (doseq [actor columns]
      (add! table actor))
    (row! table)
    (pack! table)
    table))

(defn- boolean-widget-value [widget]
  (.isChecked ^CheckBox widget))

(defn- create-boolean-widget [checked? skin]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(defn- default-widget-value [widget]
  ((get-user-object widget) 1))

(defn- truncate ^String [s limit]
  (if (> (count s) limit)
    (str (subs s 0 limit) "...")
    s))

(defn- create-default-widget [v skin]
  (label-create (truncate (binding [*print-level* nil]
                            (pr-str v))
                          60)
                skin))

(defn- enum-widget-value [widget]
  (edn/read-string (.getSelected ^SelectBox widget)))

(defn- create-enum-widget [schema v skin]
  (doto (SelectBox. ^Skin skin)
    (.setItems ^"[Ljava.lang.Object;" (into-array (map pr-str (rest schema))))
    (.setSelected (pr-str v))))

(defn text-field [text skin]
  (TextField. ^String text ^Skin skin))

(defn get-text [^TextField text-field]
  (.getText text-field))

(defn- number-widget-value [widget]
  (edn/read-string (get-text widget)))

(defn- create-number-widget [schema v skin]
  (doto (text-field (pr-str v) skin)
    (add-listener! (text-tooltip (str schema) skin))))

(defn- string-widget-value [widget]
  (get-text widget))

(defn- create-string-widget [schema v skin]
  (doto (text-field (str v) skin)
    (add-listener! (text-tooltip (str schema) skin))))

(defn- val-max-widget-value [widget]
  (edn/read-string (get-text widget)))

(defn- create-val-max-widget [schema v skin]
  (doto (text-field (pr-str v) skin)
    (add-listener! (text-tooltip (str schema) skin))))

(defn play! [sound]
  (Sound/.play sound))

(defn clear-children! [^Group group]
  (.clearChildren group))

(defn first-ascendant [^Actor actor class]
  (.firstAscendant actor class))

(defn- list-sounds-table [pad rows]
  (let [table (table-create)]
    (pad! (defaults table) pad)
    (doseq [columns rows]
      (doseq [actor columns]
        (add! table actor))
      (row! table))
    (pack! table)
    table))

(defn width! [cell fv]
  (.width ^Cell cell (float fv)))

(defn height! [cell fv]
  (.height ^Cell cell (float fv)))

(defn scroll-pane [actor skin]
  (ScrollPane. ^Actor actor ^Skin skin))

(defn- create-sound-widget [sound-names sound-name play-sound! skin stage-viewport stage-add-actor!]
  (let [table (table-create)]
    (pad! (defaults table) 5)
    (letfn [(play-sound-button [sound-name]
              (doto (create-text-button "play!" skin)
                (add-listener! (create-change-listener
                                 (fn [event _actor]
                                   (play-sound! sound-name))))))
            (choose-sound-button [sound-name]
              (doto (create-text-button sound-name skin)
                (add-listener! (create-change-listener
                                 (fn [event actor]
                                   (clear-children! table)
                                   (doseq [cell (sound-columns-fn sound-name)]
                                     (add! table (:actor cell)))
                                   (row! table)
                                   (remove! (first-ascendant actor Window))
                                   (pack! (first-ascendant table Window))
                                   (let [[k _] (get-user-object table)]
                                     (set-user-object! table [k sound-name])))))))
            (choose-sound-window []
              (let [list-table (list-sounds-table
                                 5
                                 (for [sound-name sound-names]
                                   [(choose-sound-button sound-name)
                                    (play-sound-button sound-name)]))
                    window (create-window "Choose" skin)]
                (doto (add! window (scroll-pane list-table skin))
                  (width! (+ (get-width list-table) 50))
                  (height! (min (- (get-world-height stage-viewport) 50)
                                (get-height list-table))))
                (row! window)
                (pack! window)
                (add! (get-title-table window)
                      (doto (create-text-button "X" skin)
                        (add-listener! (create-change-listener
                                         (fn [_event _actor]
                                           (remove! window))))))
                (set-modal! window true)
                window))
            (sound-columns-fn [sound-name]
              [{:actor (doto (create-text-button sound-name skin)
                         (add-listener! (create-change-listener
                                          (fn [_event _actor]
                                            (open-select-fn)))))}
               {:actor (play-sound-button sound-name)}])
            (open-select-fn []
              (stage-add-actor! (choose-sound-window)))]
      (doseq [cell (if sound-name
                     (sound-columns-fn sound-name)
                     [{:actor
                       (doto (create-text-button "No sound" skin)
                         (add-listener! (create-change-listener
                                          (fn [_event _actor]
                                            (open-select-fn)))))}])]
        (add! table (:actor cell)))
      (row! table)
      table)))

(defn- one-to-one-widget-value [widget]
  (->> (get-children widget)
       (keep get-user-object)
       first))

(defn- create-one-to-one-widget [[_ property-type] property-id editor]
  (let [table (table-create)]
    (pad! (defaults table) 5)
    (letfn [(add-one-to-one-rows [property-id]
              (let [redo-rows (fn [id]
                                (clear-children! table)
                                (add-one-to-one-rows id)
                                (pack! (first-ascendant table Window)))]
                (doseq [row (cond-> []
                              (not property-id) ; can even be nil?
                              (conj [{:actor (doto ((:editor/new-text-button editor) "+")
                                               (add-listener! (create-change-listener
                                                                (fn [event _actor]
                                                                  ((:editor/add-actor! editor)
                                                                   (property-overview-window
                                                                     property-type
                                                                     (fn [actor id]
                                                                       (remove! (first-ascendant actor Window))
                                                                       (redo-rows id))
                                                                     editor))))))}])
                              property-id
                              ; duplicated property-display-widget for a property ...
                              ; with one-to-many
                              (conj [{:actor (let [property ((:editor/get-property editor) property-id)]
                                               (doto (texture-region-image ((:editor/get-texture-region editor) 
                                                                            (or (:entity/image property)
                                                                                (first (:animation/frames (:entity/animation property))))))
                                                 (add-listener! ((:editor/text-tooltip editor) 
                                                                 (binding [*print-level* 2]
                                                                   (with-out-str
                                                                     (pprint/pprint property)))))
                                                 (set-user-object! property-id)))}]
                                    [{:actor (doto ((:editor/new-text-button editor) "-")
                                               (add-listener! (create-change-listener
                                                                (fn [event _actor]
                                                                  (redo-rows nil)))))}]))]
                  (doseq [cell row]
                    ; actor not necessary
                    (add! table (:actor cell)))
                  (row! table))))]
      (add-one-to-one-rows property-id))
    table))

(defn- one-to-many-widget-value [widget]
  (->> (get-children widget)
       (keep get-user-object)
       set))

(defn- create-one-to-many-widget [[_ property-type] property-ids skin editor]
  (let [table (table-create)]
    (pad! (defaults table) 5)
    (letfn [(add-one-to-many-rows [property-ids]
              (let [redo-rows (fn [property-ids]
                                (clear-children! table)
                                (add-one-to-many-rows property-ids)
                                ; editor window callback instead ? 
                                (pack! (first-ascendant table Window))
                                )]
                (doseq [row
                        [[{:actor (doto (create-text-button "+" skin)
                                    (add-listener! (create-change-listener
                                                     (fn [event _actor]
                                                       ((:editor/add-actor! editor)
                                                        (property-overview-window
                                                          property-type
                                                          (fn [actor id]
                                                            (remove! (first-ascendant actor Window))
                                                            (redo-rows (conj property-ids id)))
                                                          editor))))))}]
                         (for [property-id property-ids]
                           (let [property ((:editor/get-property editor) property-id)]
                             {:actor (doto (texture-region-image ((:editor/get-texture-region editor) 
                                                                  (or (:entity/image property)
                                                                      (first (:animation/frames (:entity/animation property))))))
                                       (add-listener! (text-tooltip (binding [*print-level* 2]
                                                                      (with-out-str
                                                                        (pprint/pprint property)))
                                                                    skin))
                                       (set-user-object! property-id))}))
                         (for [id property-ids]
                           {:actor (doto (create-text-button "-" skin)
                                     (add-listener! (create-change-listener
                                                      (fn [event _actor]
                                                        (redo-rows (disj property-ids id))))))})]]
                  (doseq [cell row]
                    (add! table (:actor cell)))
                  (row! table))))]
      (add-one-to-many-rows property-ids))
    table))

(defn- k-label-text [k]
  (name k))

(defn left! [cell]
  (.left ^Cell cell))

(defn- add-horiz-sep! [table colspan]
  (doto (add! table nil)
    (pad-top! 2)
    (pad-bottom! 2)
    (colspan! colspan)
    (fill-x!)
    (expand-x!))
  (row! table))

(defn- add-component-window 
  [skin columns]
  (let [window (create-window "Choose" skin)]
    (pad! (defaults window) 5)
    (add! (get-title-table window)
          (doto (create-text-button "X" skin)
            (add-listener! (create-change-listener
                             (fn [_event _actor]
                               (remove! window))))))
    (set-modal! window true)
    (doseq [{:keys [text f]} columns]
      (add! window (doto (create-text-button text skin)
                     (add-listener! (create-change-listener
                                      (fn [event _actor]
                                        (remove! window)
                                        (f))))))
      (row! window))
    (pack! window)
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
  (let [table (doto (table-create)
                (set-name! "moon.db.schema.map.ui.widget"))
        component-row-table (fn
                              [{:keys [display-remove-component-button?
                                       k]}]
                              (let [row-table (table-create)]
                                (pad! (defaults table) 2)
                                (doto (add! row-table (when display-remove-component-button?
                                                        (doto (create-text-button "-" skin)
                                                          (add-listener! (create-change-listener
                                                                           (fn [event _actor]
                                                                             (remove! (first (filter (fn [actor]
                                                                                                       (and (get-user-object actor)
                                                                                                            (= k ((get-user-object actor) 0))))
                                                                                                     (get-children table))))
                                                                             (rebuild-editor-window!)))))
                                                        (label-create "." skin)))
                                  (left!))
                                (add! row-table (label-create (k-label-text k) skin))
                                (row! row-table)
                                (pack! row-table)
                                row-table))
        add-component-row! (fn [opts]
                             (doto (add! table (component-row-table opts))
                               (right!))
                             (doto (add! table nil)
                               (pad-top! 2)
                               (pad-bottom! 2)
                               (fill-y!)
                               (expand-y!))
                             (doto (add! table (:editor-widget opts))
                               (left!))
                             (row! table))
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
    (pad! (defaults table) 5)
    (when opt?
      (doto (add! table (doto (create-text-button "Add component" skin)
                          (add-listener! (create-change-listener
                                           (fn [_event _actor]
                                             (open-add-component-window!))))))
        (colspan! colspan))
      (row! table)
      (add-horiz-sep! table colspan))
    (doseq [[i k] (map-indexed vector ks-sorted)]
      (when (pos? i)
        (add-horiz-sep! table colspan))
      (add-component-row! {:editor-widget (k->widget k)
                           :k k
                           :display-remove-component-button? (k->optional? k)}))
    table))

(require '[clojure.set :as set])

(defn play-sound!* [sounds sound-name]
  (play! (safe-get sounds sound-name)))

(defn center! [cell]
  (.center ^Cell cell))

(defn- property-editor-table [widget on-save on-delete skin]
  (let [table (table-create)]
    (pad! (defaults table) 5)
    (doto (add! table widget)
      (colspan! 2))
    (row! table)
    (doto (add! table (doto (create-text-button "Save [LIGHT_GRAY](ENTER)[]" skin)
                        (add-listener! (create-change-listener
                                         (fn [event actor]
                                           (on-save actor))))))
      (center!))
    (doto (add! table (doto (create-text-button "Delete" skin)
                        (add-listener! (create-change-listener
                                         (fn [event actor]
                                           (on-delete actor))))))
      (center!))
    (row! table)
    (pack! table)
    table))

(require '[clj-commons.pretty.repl :as pretty-repl])

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
        input (atom nil)
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
                  (text-tooltip text @skin))
                :editor/new-label
                (fn [text]
                  (label-create text @skin))
                :editor/new-window
                (fn [title]
                  (create-window title @skin))
                :editor/new-text-button
                (fn [title]
                  (create-text-button title @skin))
                :editor/get-viewport
                #(get-viewport @editor-stage)
                :editor/add-actor!
                #(add-actor! @editor-stage %)
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
                                                                     (for [widget (filter (comp vector? get-user-object) (get-children widget))
                                                                           :let [[k _] (get-user-object widget)]]
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
                                                                (let [window (-> (get-root @editor-stage)
                                                                                 (find-actor "moon.ui.clojure.editor-window"))
                                                                      map-widget-table (find-actor window "moon.db.schema.map.ui.widget")
                                                                      property (widget-value schema map-widget-table)]
                                                                  (remove! window)
                                                                  (add-actor! @editor-stage (property-editor-window property))))
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
                                                                                       (set-user-object! widget [k v])
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
                                       scroll-pane-height (get-world-height (get-viewport @editor-stage))
                                       property-id (:property/id property)
                                       with-window-close (fn [f]
                                                           (fn [actor]
                                                             (try
                                                               (swap! db f)
                                                               (remove! (first-ascendant actor Window))
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
                                       window (create-window "[SKY]Property[]" @skin)]
                                   (pad! (defaults window) 5)
                                   (let [c (add! window (scroll-pane table @skin))]
                                     (width! c (+ (get-width table) 50))
                                     (height! c (min (- scroll-pane-height 50)
                                                     (get-height table))))
                                   (row! window)
                                   (pack! window)
                                   (add! (get-title-table window)
                                         (doto (create-text-button "X" @skin)
                                           (add-listener! (create-change-listener
                                                            (fn [_event _actor]
                                                              (remove! window))))))
                                   (set-modal! window true)
                                   (add-actor! window (proxy [Actor] []
                                                        (act [delta]
                                                          (when (key-just-pressed? @input Input$Keys/ENTER)
                                                            (on-save this))
                                                          (let [^Actor this this]
                                                            (proxy-super act delta)))
                                                        (draw [batch parent-alpha])))
                                   (set-name! window "moon.ui.clojure.editor-window")
                                   window))
        main-window (fn []
                      (let [window (create-window "Edit" @skin)]
                        (doseq [property-type property-types]
                          (add! window (doto (create-text-button (str/capitalize (name property-type)) @skin)
                                         (add-listener! (create-change-listener
                                                          (fn [_event _actor]
                                                            (add-actor! @editor-stage
                                                                        (property-overview-window
                                                                          property-type
                                                                          (fn [_actor id]
                                                                            (add-actor! @editor-stage
                                                                                        (property-editor-window ((:editor/get-property editor) id))))
                                                                          editor)))))))
                          (row! window))
                        (pack! window)
                        window))]
    {:create!
     (fn [{:keys [audio files] :as capabilities}] 
       (reset! input (:input capabilities))
       (reset! batch (sprite-batch!))
       (reset! skin (create-skin (internal files (:skin editor-config))))
       (set! (.markupEnabled (.getData (.getFont ^Skin @skin "default-font"))) true)
       (reset! editor-stage (Stage. (FitViewport. (float 1440) (float 900)) @batch))
       (reset! sounds (into {}
                            (for [path (-> (:sounds editor-config) io/resource slurp edn/read-string)]
                              [path (new-sound audio (internal files path))])))
       (reset! textures (create-textures files (:textures editor-config)))
       (set-input-processor! (:input capabilities) @editor-stage)
       (add-actor! @editor-stage (main-window)))
     :dispose!
     (fn []
       (run! dispose! (vals @sounds))
       (dispose! @batch)
       (dispose! @skin)
       (run! dispose! (vals @textures)))
     :render!
     (fn [_capabilities]
       (clear-screen! 0 0 0 0)
       (act! @editor-stage)
       (draw! @editor-stage))
     :resize!
     (fn [width height]
       (resize! (get-viewport @editor-stage) width height true))
     }))

(extend-type TiledMapTileLayer
  GetName
  (get-name [this]
    (.getName this)))

(extend-type Actor
  GetName
  (get-name [this]
    (.getName this)))

(import '(com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration))

(defn create-capabilities []
  {
   :audio    Gdx/audio
   :files    Gdx/files
   :graphics Gdx/graphics
   :input    Gdx/input
   })

(defn lwjgl3-application! 
  [{:keys [create! 
           dispose! 
           render! 
           resize!]}
   {:keys [title 
           width 
           height 
           fps]}]
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. 
    (reify ApplicationListener
      (create [_]
        (create! (create-capabilities)))

      (dispose [_]
        (dispose!))

      (render [_]
        (render! (create-capabilities)))

      (resize [_ width height]
        (resize! width height))

      (pause [_])

      (resume [_])) 
    (doto (Lwjgl3ApplicationConfiguration.)
      (.setTitle title)
      (.setWindowedMode width height)
      (.setForegroundFPS fps))))

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
     (fn [{:keys [files]}]
       (reset! batch (sprite-batch!))
       (reset! font (create-default-font! files
                                          {:path "fonts/films.EXL_____.ttf"
                                           :size 16
                                           :quality-scaling 2
                                           :use-integer-positions? false}))
       (reset! shape-drawer-texture (white-pixel-texture))
       (reset! shape-drawer (ShapeDrawer. @batch
                                          (TextureRegion. ^Texture @shape-drawer-texture 1 0 1 1)))
       (reset! viewport (FitViewport. (float 1440)
                                      (float 900)
                                      (doto (OrthographicCamera.)
                                        (.setToOrtho false 1440 900)))))

     :dispose!
     (fn []
       (dispose! @batch)
       (dispose! @font)
       (dispose! @shape-drawer-texture))

     :render!
     (fn [{:keys [input]}]
       (when (key-just-pressed? input Input$Keys/U)
         (swap! unit-scale #(if (== ^double % 1.0)
                              world-unit-scale
                              1.0)))
       (clear-screen! 0.08 0.08 0.1 1)
       (set-projection-matrix! @batch (.combined ^OrthographicCamera (get-camera @viewport)))
       (Batch/.begin @batch)
       (let [b @batch
             f @font
             us @unit-scale
             sd @shape-drawer
             marker-color (float-bits 1 0.3 0.3 1)
             guide-color (float-bits 0.35 0.35 0.45 1)]
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
       (Batch/.end @batch))

     :resize!
     (fn [width height]
       (resize! @viewport width height true))}))

(defn draw-text-demo! []
  (lwjgl3-application! (draw-text-demo-listener)
                       {:title "draw-text! demo"
                        :width 1440
                        :height 900
                        :fps 60}))

(defn levelgen! []
  (lwjgl3-application! (levelgen-listener)
                       {:title "Levelgen", 
                        :width 1440, 
                        :height 900, 
                        :fps 60}))


(defn editor! []
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
        listener (game-listener
                   {
                    :game-schemas game-schemas
                    :game-db game-db
                    :info-text info-text
                    :level-fn ;create-vampire-level
                    ;create-uf-caves
                    create-modules-level
                    :pausing? true
                    :state->pause-game? {:active-skill false
                                         :stunned false
                                         :player-moving false
                                         :player-idle true
                                         :player-dead true
                                         :player-item-on-cursor true}
                    :controls {:zoom-in Input$Keys/MINUS
                               :zoom-out Input$Keys/EQUALS
                               :unpause-once Input$Keys/P
                               :unpause-continously Input$Keys/SPACE
                               :close-windows-key Input$Keys/ESCAPE
                               :toggle-inventory Input$Keys/I
                               :toggle-entity-info Input$Keys/E}
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
    (lwjgl3-application! listener config)))
