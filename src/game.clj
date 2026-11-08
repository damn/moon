(ns game
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.math :as math]
            [clojure.string :as str]
            [clojure.pprint :as pprint]
            [lib.mods :as mods]
            [lib.number :as number]  
            [lib.position :as position]
            [lib.raycaster :as raycaster]
            [lib.rectangle :as rectangle]
            [lib.schema :as schema]
            [lib.shape-drawer :as shape-drawer]
            [lib.stats :as stats]
            [lib.textures :as textures]
            [lib.tiled-map :as tiled-map]
            [lib.timer :as timer]
            [malli.core :as m]
            [malli.error :as me]
            [ui.action-bar :as action-bar]
            [ui.dev-menu :as dev-menu]
            [lib.v2 :as v2]
            [lib.val-max :as val-max]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx ApplicationListener Files Gdx Graphics Input Input$Buttons Input$Keys InputProcessor)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color Colors Cursor OrthographicCamera Pixmap Pixmap$Format Texture Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d Batch BitmapFont BitmapFont$BitmapFontData SpriteBatch TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.maps MapLayer MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap TiledMapTile TiledMapTileLayer TiledMapTileLayer$Cell TmxMapLoader)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)
           (com.badlogic.gdx.math Circle Intersector Rectangle Vector2 Vector3)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup HorizontalGroup Image Label Skin Stack Table TextButton TextTooltip TooltipManager Widget Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener ClickListener Drawable TextureRegionDrawable)
           (com.badlogic.gdx.utils Align Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport Viewport)
           (java.util Random))
  (:gen-class))

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
                      (mapcat position/get-8-neighbours
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

(defn create-db [schemas properties]
  (assert (or (empty? properties)
              (apply distinct? (map :property/id properties))))
  (doseq [property properties]
    (let [schema (m/schema (schema/malli-form (get schemas (keyword "properties" (namespace (:property/id property)))) schemas))]
      (when-not (m/validate schema property)
        (throw (ex-info (str (me/humanize (m/explain schema property)))
                        {:value property
                         :schema (m/form schema)})))))
  (zipmap (map :property/id properties) properties))

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

(defn get-raw [db property-id]
  (assert (contains? db property-id))
  (get db property-id))

(defn all-raw [db property-type]
  (->> (vals db)
       (filter #(= property-type (keyword "properties" (namespace (:property/id %)))))))

(declare build-values)

(defn- create-value [[k] v schemas db]
  (case k
    :s/map
    (build-values schemas v db)

    :s/one-to-many
    (set (map (fn [property-id]
                (build-values schemas
                              (get-raw db property-id)
                              db))
              v))

    :s/one-to-one
    (build-values schemas
                  (get-raw db v)
                  db)

    v))

(defn build-values [schemas property db]
  (reduce (fn [m k]
            (assoc m k
                   (try (create-value (get schemas k) (k m) schemas db)
                        (catch Throwable t
                          (throw (ex-info " " {:k k
                                               :v (k m)} t))))))
          property
          (keys property)))

(defn build [db schemas property-id]
  (build-values schemas
                (get-raw db property-id)
                db))

(defn create-vampire-level [_]
  {:tiled-map (.load (TmxMapLoader.) "maps/vampire.tmx")
   :start-position [32 71]})

(defn- inv-window-cell [world on-click-cell slot->drawable draw-cell-rect! cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (Stack.)]
       (run! #(.addActor ^Group stack ^Actor %)
             [(proxy [Widget] []
                (draw [batch parent-alpha]
                  (when-let [stage (.getStage ^Actor this)]
                    (draw-cell-rect! @(:world/player-eid @world)
                                     (.getX ^Actor this)
                                     (.getY ^Actor this)
                                     (let [v2 (.unproject (.getViewport ^Stage stage)
                                                         (Vector2. (float (.getX Gdx/input))
                                                                   (float (.getY Gdx/input))))
                                           local (.stageToLocalCoordinates ^Actor this
                                                                           (Vector2. (.x v2) (.y v2)))
                                           x (.x ^Vector2 local)
                                           y (.y ^Vector2 local)]
                                       (.hit ^Actor this (float x) (float y) true))
                                     (.getUserObject ^Actor (.getParent ^Actor this))))))
              (doto (Image. ^Drawable background-drawable)
                (.setName "image-widget")
                (.setUserObject {:background-drawable background-drawable
                                 :cell-size cell-size}))])
       (doto stack
         (.addListener (proxy [ClickListener] []
                         (clicked [event _x _y]
                           (on-click-cell event cell))))
         (.setName "inventory-cell")
         (.setUserObject cell)))}))

(defn- build-inventory-window
  [{:keys [world
           on-click-cell
           draw-cell-rect!
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (TextureRegionDrawable. ^TextureRegion (slot->texture-region slot))
                           (.setMinSize cell-size cell-size)
                           (.tint ^Color (Color. 1 1 1 0.4))))
        ->cell (partial inv-window-cell world on-click-cell slot->drawable draw-cell-rect! cell-size)
        cell-table (Table.)
        window (Window. "Inventory" ^Skin skin)]
    (doseq [row (concat [[{:actor nil} {:actor nil}
                          (->cell :inventory.slot/helm)
                          (->cell :inventory.slot/necklace)]
                         [{:actor nil}
                          (->cell :inventory.slot/weapon)
                          (->cell :inventory.slot/chest)
                          (->cell :inventory.slot/cloak)
                          (->cell :inventory.slot/shield)]
                         [{:actor nil} {:actor nil}
                          (->cell :inventory.slot/leg)]
                         [{:actor nil}
                          (->cell :inventory.slot/glove)
                          (->cell :inventory.slot/rings :position [0 0])
                          (->cell :inventory.slot/rings :position [1 0])
                          (->cell :inventory.slot/boot)]]
                        (for [y (range 4)]
                          (for [x (range 6)]
                            (->cell :inventory.slot/bag :position [x y]))))]
      (doseq [cell row]
        (.add cell-table ^Actor (:actor cell)))
      (.row cell-table))
    (.pack cell-table)
    (.setName cell-table "inventory-cell-table")
    (let [c (.add window cell-table)]
      (.pad c (float 4)))
    (.row window)
    (.pack window)
    (.setName window "moon.ui.windows.inventory")
    (.setVisible window false)
    (let [[x y] position]
      (.setPosition window (float x) (float y)))
    window))

(defn create-inventory-window
  [on-click-cell draw-cell-rect! skin stage textures world]
  (let [slot->y-sprite-idx #:inventory.slot {:weapon 0
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
        slot->texture-region (fn [slot]
                               (let [width 48
                                     height 48
                                     sprite-x 21
                                     sprite-y (+ (slot->y-sprite-idx slot) 2)
                                     bounds [(* sprite-x width)
                                             (* sprite-y height)
                                             width
                                             height]]
                                 (textures/texture-region textures
                                                          {:image/file "images/items.png"
                                                           :image/bounds bounds})))
        cell-size 48]
    (build-inventory-window
     {:world world
      :on-click-cell on-click-cell
      :draw-cell-rect! draw-cell-rect!
      :skin skin
      :position [(.getWorldWidth (.getViewport ^Stage stage))
                 (.getWorldHeight (.getViewport ^Stage stage))]
      :slot->texture-region slot->texture-region
      :cell-size cell-size})))

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
  (->> (let [idx (-> center-entity
                     :moon.content-grid/content-cell
                     deref
                     :idx)]
         (cons idx (position/get-8-neighbours idx)))
       (keep #(get-in grid %))
       (mapcat (comp :entities deref))))

(defn- sshuffle
  "Return a random permutation of coll"
  [coll random]
  (let [al (java.util.ArrayList. ^java.util.Collection coll)]
    (java.util.Collections/shuffle al random)
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
        (let [current-order (if (< (.nextFloat ^Random random) turn-ratio)
                              (create-order)
                              current-order)
              neighbours (position/get-4-neighbours (last posi-seq))
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

(def k->adj-num
  {:wide
   (fn [open-paths random]
     (if (= open-paths 1)
       (case (int (* 3 (.nextFloat ^Random random)))
         0 1
         2)
       (case (int (* 4 (.nextFloat ^Random random)))
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
       (case (int (* 7 (.nextFloat ^Random random)))
         0 0
         1 2
         1)))

   :default
   ; etwas breiter als 1 aber immernoch zu dünn für m ein game -> turn-ratio verringern besser
   (fn [open-paths random]
     (if (= open-paths 1)
       (case (int (* 4 (.nextFloat ^Random random)))
         0 1
         1 1
         2 1
         3 2
         1)
       (case (int (* 4 (.nextFloat ^Random random)))
         0 0
         1 1
         2 1
         3 2
         1)))})

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

(def ^:private transition-idxvalues [2 8 1 4])

(defn- transition-idx-value [position position->transition?]
  (->> position
       position/get-4-neighbours
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
        props (.getProperties ^TiledMap modules-tiled-map)
        _ (assert (and (= (.get props "width")
                          (* number-modules-x (+ modules-width module-offset-tiles)))
                       (= (.get props "height")
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
  [^TiledMap schema-tiled-map grid]
  (let [copy-tile (memoize
                   (fn [tile]
                     (assert tile)
                     (if (instance? StaticTiledMapTile tile)
                       (StaticTiledMapTile. ^StaticTiledMapTile tile)
                       (StaticTiledMapTile. ^TextureRegion tile))))]
    {:properties (merge (let [props (.getProperties schema-tiled-map)]
                          (zipmap (.getKeys props)
                                  (.getValues props)))
                        {"width" (count grid)
                         "height" (count (grid 0))})
     :layers (for [layer (.getLayers schema-tiled-map)]
               {:name (.getName ^TiledMapTileLayer layer)
                :visible? (.isVisible ^TiledMapTileLayer layer)
                :properties (let [props (.getProperties ^TiledMapTileLayer layer)]
                              (zipmap (.getKeys ^MapProperties props)
                                      (.getValues ^MapProperties props)))
                :tiles (for [x (range (count grid))
                             y (range (count (grid 0)))
                             :let [position [x y]
                                   local-position (get-in grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (.getCell ^TiledMapTileLayer layer (int (local-position 0)) (int (local-position 1)))]
                             [position (copy-tile (.getTile ^TiledMapTileLayer$Cell cell))])))})}))

(defn- create-tiled-map* [schema-tiled-map scaled-grid]
  (let [{:keys [properties layers]} (grid->tiled-map schema-tiled-map scaled-grid)
        tiled-map (TiledMap.)]
    (doseq [[k v] properties]
      (assert (string? k))
      (.put (.getProperties tiled-map) k v))
    (doseq [{:keys [name visible? properties tiles]} layers]
      (assert (string? name))
      (assert (boolean? visible?))
      (let [props (.getProperties tiled-map)
            ^TiledMapTileLayer layer (doto (TiledMapTileLayer. (int (.get props "width"))
                                                               (int (.get props "height"))
                                                               (int (.get props "tilewidth"))
                                                               (int (.get props "tileheight")))
                                       (.setName ^String name)
                                       (.setVisible visible?))]
        (doseq [[k v] properties]
          (assert (string? k))
          (.put ^MapProperties (.getProperties layer) k v))
        (doseq [[[x y] tile] tiles
                :when tile]
          (.setCell layer (int x) (int y)
                    (doto (TiledMapTileLayer$Cell.)
                      (.setTile ^TiledMapTile tile))))
        (.add (.getLayers tiled-map) ^MapLayer layer)))
    tiled-map))

(defn create-modules-level
  [{:keys [level/creature-properties
           world/map-size
           world/spawn-rate]
    :or {map-size 5
         spawn-rate 0.05}}]
  (let [scale [32 20]
        {:keys [start grid]} (create-caves (Random.) map-size map-size :wide)
        grid (fix-nads grid)
        grid (let [grid (reduce #(assoc-in %1 %2 :transition)
                                grid
                                (filter (fn [position]
                                          (and (= :wall (get-in grid position))
                                               (some #(= :ground (get-in grid %))
                                                     (position/get-8-neighbours position))))
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
        can-spawn? #(= "all" (tiled-map/movement-property tiled-map %))
        _ (assert (can-spawn? start-position))
        spawn-positions (flood-fill scaled-grid start-position can-spawn?)
        creatures (for [position spawn-positions
                        :when (and (not= position start-position)
                                   (<= (rand) spawn-rate)
                                   (seq creature-properties))]
                    [position (rand-nth creature-properties)])]
    (tiled-map/add-creatures-layer! tiled-map creatures)
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
      (.put (.getProperties tiled-map) k v))
    (doseq [{:keys [name visible? properties tiles]} layers]
      (assert (string? name))
      (assert (boolean? visible?))
      (let [props (.getProperties tiled-map)
            ^TiledMapTileLayer layer (doto (TiledMapTileLayer. (int (.get props "width"))
                                                               (int (.get props "height"))
                                                               (int (.get props "tilewidth"))
                                                               (int (.get props "tileheight")))
                                       (.setName ^String name)
                                       (.setVisible visible?))]
        (doseq [[k v] properties]
          (assert (string? k))
          (.put ^MapProperties (.getProperties layer) k v))
        (doseq [[[x y] tile] tiles
                :when tile]
          (.setCell layer (int x) (int y)
                    (doto (TiledMapTileLayer$Cell.)
                      (.setTile ^TiledMapTile tile))))
        (.add (.getLayers tiled-map) ^MapLayer layer)))
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
        {:keys [start grid]} (create-caves (Random.) cave-size cave-size cave-style)
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
                               tile (StaticTiledMapTile. ^TextureRegion texture-region)]
                           (.put ^MapProperties (.getProperties ^StaticTiledMapTile tile)
                                 "movement" movement)
                           tile))))
        _ (assert (= #{:wall :ground} (set (apply concat grid))))
        {:keys [start-position grid]} (scale-grid grid start scaling)
        grid (let [grid (reduce #(assoc-in %1 %2 :transition) grid
                                (filter (fn [position]
                                          (and (= :wall (get-in grid position))
                                               (some #(= :ground (get-in grid %))
                                                     (position/get-8-neighbours position))))
                                        (for [x (range (count grid)) y (range (count (grid 0)))] [x y])))]
               (assert (or
                        (= #{:wall :ground :transition} (set (apply concat grid)))
                        (= #{:ground :transition} (set (apply concat grid))))
                       (str "(set (apply concat grid)): " (set (apply concat grid))))
               grid)
        position->tile (position-tile-fn grid)
        tiled-map (create-tiled-map grid tile-size create-tile position->tile)
        can-spawn? #(= "all" (tiled-map/movement-property tiled-map %))
        _ (assert (can-spawn? start-position))
        level (inc (rand-int 6))
        creatures (filter #(= level (:creature/level %)) creature-properties)
        spawn-positions (flood-fill grid start-position can-spawn?)
        creatures (for [position spawn-positions
                        :when (and (not= position start-position)
                                   (<= (rand) spawn-rate))]
                    [position (rand-nth creatures)])]
    (tiled-map/add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position start-position}))

(def slots
  #{:inventory.slot/bag
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

(def ^:private info-text-config
  {:k->fn {:creature/level (fn [v _elapsed-time]
                              (str "Level: " v))
           :entity/stats (fn [entity-stats _elapsed-time]
                           (stats/format-text entity-stats))
           :effects.target/convert (fn [_ _elapsed-time]
                                    "Converts target to your side.")
           :effects.target/damage (fn [{[min max] :damage/min-max} _elapsed-time]
                                    (str min "-" max " damage"))
           :effects.target/kill (fn [_ _elapsed-time]
                                  "Kills target")
           :effects.target/melee-damage (fn [_ _elapsed-time]
                                          "Damage based on entity strength.")
           :effects.target/spiderweb (fn [_ _elapsed-time]
                                       "Spiderweb slows 50% for 5 seconds.")
           :effects.target/stun (fn [duration _elapsed-time]
                                  (str "Stuns for " (number/readable duration) " seconds"))
           :effects/spawn (fn [{:keys [property/pretty-name]} _elapsed-time]
                            (str "Spawns a " pretty-name))
           :effects/target-all (fn [_ _elapsed-time]
                                 "All visible targets")
           :entity/delete-after-duration (fn [counter elapsed-time]
                                             (str "Remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :entity/faction (fn [faction _elapsed-time]
                             (str "Faction: " (name faction)))
           :entity/fsm (fn [fsm _elapsed-time]
                          (str "State: " (name (:state fsm))))
           :stats/modifiers (fn [mods _elapsed-time]
                              (mods/format-text mods))
           :entity/skills (fn [skills _elapsed-time]
                            (when (seq skills)
                              (str "Skills: " (str/join "," (map name (keys skills))))))
           :entity/species (fn [species _elapsed-time]
                             (str "Creature - " (str/capitalize (name species))))
           :entity/temp-modifier (fn [{:keys [counter]} elapsed-time]
                                    (str "Spiderweb - remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :projectile/piercing? (fn [_ _elapsed-time]
                                   "Piercing")
           :property/pretty-name (fn [v _elapsed-time]
                                    v)
           :skill/cooling-down? (fn [counter elapsed-time]
                                   (str "Cooldown: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :skill/action-time (fn [v _elapsed-time]
                                 (str "Action-Time: " (number/readable v) " seconds"))
           :skill/action-time-modifier-key (fn [v _elapsed-time]
                                              (case v
                                                :stats/cast-speed "Spell"
                                                :stats/attack-speed "Attack"))
           :skill/cooldown (fn [v _elapsed-time]
                             (str "Cooldown: " (number/readable v) " seconds"))
           :skill/cost (fn [v _elapsed-time]
                          (str "Cost: " v " Mana"))
           :maxrange (fn [v _elapsed-time]
                       (str "Range: " v " Meters."))}
   :k-order-index (zipmap [:property/pretty-name
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
   :k->colors {:property/pretty-name "PRETTY_NAME"
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
               :entity/temp-modifier "LIGHT_GRAY"}})

(defn- remove-newlines [s]
  (let [new-s (-> s
                  (str/replace "\n\n" "\n")
                  (str/replace #"^\n" "")
                  str/trim-newline)]
    (if (= (count new-s) (count s))
      s
      (remove-newlines new-s))))

; TODO used for item/skill/entity
; logic should be in inventory window / action-bar and entity info window !
(defn info-text
  [entity elapsed-time]
  (let [{:keys [k->fn
                k-order-index
                k->colors]} info-text-config
        component-info (fn [[k v]]
                         (let [s (if-let [info-fn (k->fn k)]
                                   (str (info-fn v elapsed-time)))]
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

(defn- inv-get-cell [inventory-window cell]
  (->> (.getChildren ^Group (.findActor ^Group inventory-window "inventory-cell-table"))
       (filter #(= (.getUserObject ^Actor %) cell))
       first))

(defn set-item!* [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (inv-get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")
        cell-size (:cell-size (.getUserObject ^Actor image-widget))]
    (.setDrawable ^Image image-widget ^Drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                                  (.setMinSize cell-size cell-size)))
    (.addListener ^Actor cell-widget (TextTooltip. ^String tooltip-text ^Skin skin))
    nil))

(defn item-info-text [item]
  (assert (valid? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (mods/format-text (:stats/modifiers item)) "[]"))])))

(defn set-item! [skin stage textures cell item]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (set-item!* cell
                  {:texture-region (textures/texture-region textures (:entity/image item))
                   :tooltip-text (item/info-text item)}
                  skin)))

(defn set-skill! [skin stage textures elapsed-time skill]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.action-bar")
      (action-bar/add-skill! {:skill-id (:property/id skill)
                              :texture-region (textures/texture-region textures (:entity/image skill))
                              :tooltip-text (info-text skill elapsed-time)}
                             skin)))

(defn remove-item!* [inventory-window cell]
  (let [cell-widget (inv-get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")]
    (.setDrawable ^Image image-widget ^Drawable (:background-drawable (.getUserObject ^Actor image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn remove-item! [stage cell]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (remove-item!* cell)))

(defn sync-player-ui! [skin stage textures elapsed-time eid]
  (doseq [skill (vals (:entity/skills @eid))]
    (set-skill! skin stage textures elapsed-time skill))
  (doseq [[slot grid] (:entity/inventory @eid)
          x (range (count grid))
          y (range (count (grid 0)))
          :let [position [x y]
                item (get-in grid position)]
          :when item]
    (set-item! skin stage textures [slot position] item)))

(defn toggle-inventory-visible! [stage]
  (let [inventory (-> (.getRoot ^Stage stage)
                      (.findActor "moon.ui.windows.inventory"))]
    (.setVisible ^Actor inventory (not (.isVisible ^Actor inventory)))))

(defn close-windows! [stage]
  (->> (.getChildren ^Group (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows"))
       (run! #(.setVisible ^Actor % false))))

(defn toggle-entity-info! [stage]
  (let [entity-info (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows.entity-info")]
    (.setVisible ^Actor entity-info
                 (not (.isVisible ^Actor entity-info)))))

(defn mouseover-actor [stage x y]
  (.hit ^Stage stage (float x) (float y) true))

(defn ui-mouse-position [stage]
  (let [v2 (.unproject (.getViewport ^Stage stage)
                       (Vector2. (float (.getX Gdx/input))
                                 (float (.getY Gdx/input))))]
    [(.x v2) (.y v2)]))

(defn show-modal! [^Skin skin ^Stage stage {:keys [title text button-text on-click]}]
  (assert (not (.findActor (.getRoot stage) "moon.ui.modal-window")))
  (let [^String title title
        ^String text text
        window (Window. title skin)]
    (.add window (Label. text skin))
    (.row window)
    (.add window (doto (TextButton. ^String button-text skin)
                   (.addListener (proxy [ChangeListener] []
                                   (changed [_event _actor]
                                     (.remove (.findActor (.getRoot stage)
                                                          "moon.ui.modal-window"))
                                     (on-click))))))
    (.row window)
    (.pack window)
    (.setModal window true)
    (.setName window "moon.ui.modal-window")
    (.setPosition window
                  (/ (.getWorldWidth (.getViewport stage)) 2)
                  (float (* (.getWorldHeight (.getViewport stage)) (/ 3 4)))
                  (float Align/center))
    (.addActor stage window)))

(defn- create-info-window
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [^Skin skin skin
        label (Label. "MY LABEL TEXT" skin)
        window (Window. ^String title skin)
        c (.add window label)]
    (.expand c)
    (.row window)
    (.pack window)
    (.setName window actor-name)
    (.setVisible window visible?)
    (let [[x y] position]
      (.setPosition window (float x) (float y)))
    (.addActor window (proxy [Actor] []
                         (act [delta]
                           (let [^Actor this this]
                             (when (.getStage this)
                               (.setText label ^String (set-label-text!)))
                             (.pack window)
                             (proxy-super act delta)))
                         (draw [batch parent-alpha])))
    window))

(defn info-window-create
  [skin stage mouseover-eid world]
  (create-info-window
   {:title "Entity Info"
    :actor-name "moon.ui.windows.entity-info"
    :visible? false
    :position [(.getWorldWidth (.getViewport ^Stage stage)) 0]
    :set-label-text! (fn []
                       (if-let [eid @mouseover-eid]
                         (info-text (apply dissoc @eid [:entity/skills
                                                        :entity/faction
                                                        :active-skill])
                                    @(:world/elapsed-time @world))
                         ""))
    :skin skin}))

(defn nearest-entity [cell faction]
  (-> cell faction :eid))

(defn nearest-entity-distance [cell faction]
  (-> cell faction :distance))

(defn cached-adjacent-cells [grid cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (into [] (keep #(get-in grid %)) (position/get-8-neighbours (:position @cell)))]
      (swap! cell assoc :adjacent-cells result)
      result)))

(defn nearest-enemy [grid entity]
  (nearest-entity @(get-in grid (mapv int (:entity/position entity)))
                  (enemy (:entity/faction entity))))

(defn nearest-enemy-distance [grid entity]
  (nearest-entity-distance @(get-in grid (mapv int (:entity/position entity)))
                           (enemy (:entity/faction entity))))

(defn add-text-effect [entity elapsed-time text duration]
  (assoc entity :entity/string-effect
         (if-let [existing (:entity/string-effect entity)]
           (-> existing
               (update :text str "\n" text)
               (update :counter timer/increment duration))
           {:text text
            :counter (timer/create elapsed-time duration)})))

(defn- inventory-cell-path [[slot [x y]]]
  [:entity/inventory slot x y])

(defn set-item [entity cell item]
  (assert (and (nil? (get-in entity (inventory-cell-path cell)))
               (valid-slot? cell item)))
  (cond-> (assoc-in entity (inventory-cell-path cell) item)
    (applies-modifiers? cell)
    (update :entity/stats stats/add-mods (:stats/modifiers item))))

(defn remove-item [entity cell]
  (let [item (get-in entity (inventory-cell-path cell))]
    (assert item)
    (cond-> (assoc-in entity (inventory-cell-path cell) nil)
      (applies-modifiers? cell)
      (update :entity/stats stats/remove-mods (:stats/modifiers item)))))

(defn float-bits [r g b a]
  (Color/toFloatBits (float r) (float g) (float b) (float a)))

(defrecord Cell 
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
  (v2/direction (:entity/position entity)
                (:entity/position other-entity)))

(defn start-point [entity target-entity]
  (mapv + (:entity/position entity)
        (v2/scale (direction entity target-entity)
                  (/ (:entity/width entity) 2))))

(defn end-point [entity target-entity maxrange]
  (mapv + (start-point entity target-entity)
        (v2/scale (direction entity target-entity)
                  maxrange)))

(defn in-range? [entity target-entity maxrange]
  (< (- (float (v2/distance (:entity/position entity)
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
  (rectangle/touched-tiles
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))

(let [order (position/get-8-neighbours [0 0])
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
      (v2/direction position (:entity/position @target-entity))

      (nil? target-cell)
      nil

      :else
      (when-not (and (= target-cell own-cell)
                     (occupied-by-other? @own-cell eid))
        (when-not (inside-cell? grid @eid target-cell)
          (v2/direction position (:middle @target-cell)))))))

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
                                    (float (if (position/diagonal? (:position cell*)
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

(defn register-eid!
  [{:keys [world/id-counter
           world/entity-ids
           world/content-grid
           world/grid]}
   eid]
  (assert (and (not (contains? @eid :entity/id))))
  (let [id (swap! id-counter inc)]
    (assert (number? id))
    (swap! eid assoc :entity/id id)
    (swap! entity-ids assoc id eid))

  (assert (:entity/position @eid))
  (update-entity! content-grid eid)

  (assert (:entity/position @eid))
  (when (:entity/collides? @eid)
    (assert (valid-position? grid @eid (:entity/id @eid))))
  (set-touched-cells! grid eid)
  (when (:entity/collides? @eid)
    (set-occupied-cells! grid eid))
  nil)

(defn relocate-eid!
  [{:keys [world/content-grid
           world/grid]}
   eid]
  (update-entity! content-grid eid)
  (remove-from-touched-cells! grid eid)
  (set-touched-cells! grid eid)
  (when (:entity/collides? @eid)
    (remove-from-occupied-cells! grid eid)
    (set-occupied-cells! grid eid))
  nil)

(defn unregister-eid!
  [{:keys [world/entity-ids
           world/content-grid
           world/grid]}
   eid]
  (let [id (:entity/id @eid)]
    (swap! entity-ids dissoc id)
    (remove-entity! content-grid eid)
    (remove-from-touched-cells! grid eid)
    (when (:entity/collides? @eid)
      (remove-from-occupied-cells! grid eid)))
  nil)

(defn cell-at [world [x y]]
  (when-let [cell-atom (get-in (:world/grid world) [x y])]
    @cell-atom))

(defn cells-at [world tile-positions]
  (for [[x y] tile-positions
        :let [cell (cell-at world [x y])]
        :when cell]
    [[x y] cell]))

(defn- create-grid [^TiledMap tiled-map]
  (let [props (.getProperties tiled-map)
        w (.get props "width")
        h (.get props "height")
        xyfn (fn [position]
               (atom
                (map->Cell
                 {:position position
                  :middle (mapv (partial + 0.5) position)
                  :movement (case (tiled-map/movement-property tiled-map position)
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

(defn create-world
  [^TiledMap tiled-map {:keys [z-orders minimum-size start-position]}]
  (let [props (.getProperties tiled-map)
        width (.get props "width")
        height (.get props "height")
        grid (create-grid tiled-map)]
    {:world/tiled-map tiled-map
     :world/start-position start-position
     :world/player-eid nil
     :world/potential-field-cache (atom nil)
     :world/id-counter (atom 0)
     :world/entity-ids (atom {})
     :world/grid grid
     :world/content-grid (create-content-grid width height 16)
     :world/explored-tile-corners (atom
                                   (mapv (fn [x] (mapv (fn [y] false)
                                                       (range height)))
                                         (range width)))
     :world/raycaster (create-raycaster grid)
     :world/elapsed-time (atom 0)
     :world/z-orders z-orders
     :world/minimum-size minimum-size}))

; belongs to game - further down?
(def ^:private controls
  {:zoom-in Input$Keys/MINUS
   :zoom-out Input$Keys/EQUALS
   :unpause-once Input$Keys/P
   :unpause-continously Input$Keys/SPACE
   :close-windows-key Input$Keys/ESCAPE
   :toggle-inventory Input$Keys/I
   :toggle-entity-info Input$Keys/E})

(def ^:private controls-info
  (str/join "\n"
            ["[W][A][S][D] - Move"
             "[ESCAPE] - Close windows"
             "[I] - Inventory window"
             "[E] - Entity Info window"
             "[-]/[=] - Zoom"
             "[P]/[SPACE] - Unpause"
             "Leftmouse click - use skill/drop item on cursor"]))

; viewport fns
(def ^:private key-pressed-actions
  {(:zoom-in controls) (fn [{:keys [viewport]}]
                         (let [^OrthographicCamera camera (.getCamera ^Viewport @viewport)]
                           (set! (.zoom camera) (max 0.1 (+ (.zoom camera) 0.025)))
                           (.update camera)))
   (:zoom-out controls) (fn [{:keys [viewport]}]
                          (let [^OrthographicCamera camera (.getCamera ^Viewport @viewport)]
                            (set! (.zoom camera) (max 0.1 (+ (.zoom camera) -0.025)))
                            (.update camera)))})

; stage fns
(def ^:private key-just-pressed-actions
  {(:close-windows-key controls) (fn [{:keys [stage]}]
                                   (close-windows! @stage))
   (:toggle-inventory controls) (fn [{:keys [stage]}]
                                  (toggle-inventory-visible! @stage))
   (:toggle-entity-info controls) (fn [{:keys [stage]}]
                                    (toggle-entity-info! @stage))})

; pass input?
(defn world-mouse-position [viewport]
  (let [v2 (.unproject ^Viewport viewport
                       (Vector2. (float (.getX Gdx/input))
                                 (float (.getY Gdx/input))))]
    [(.x v2) (.y v2)]))

(defn- button?
  [actor]
  (let [button-class? (fn [a] (some #(= Button %) (supers (class a))))]
    (or (button-class? actor)
        (when-let [parent (.getParent ^Actor actor)]
          (button-class? parent)))))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (.getParent ^Actor actor)
                            (= "inventory-cell" (.getName ^Actor (.getParent ^Actor actor)))
                            (.getUserObject ^Actor (.getParent ^Actor actor)))]
    (cond
      inventory-slot
      [:mouseover-actor/inventory-cell inventory-slot]

      (when (instance? Label actor)
        (when-let [p (.getParent ^Actor actor)]
          (when-let [p (.getParent ^Actor p)]
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

(defn- white-pixel-texture []
  (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                 (.setColor 1 1 1 1)
                 (.drawPixel 0 0))
        texture (Texture. pixmap)]
    (.dispose pixmap)
    texture))

(defn- draw-fn-text [batch default-font unit-scale {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        scale (or scale 1)
        font-data (.getData ^BitmapFont font)
        old-scale (.scaleX ^BitmapFont$BitmapFontData font-data)
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
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

(defn- draw-fn-texture-region [batch unit-scale texture-region [x y] & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(.getRegionWidth ^TextureRegion texture-region)
                                (.getRegionHeight ^TextureRegion texture-region)]]
                  (if (= @unit-scale 1)
                    dimensions
                    (mapv (comp float (partial * @unit-scale))
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

(defn- item-place-position [player-position world-mouse-position maxrange]
  (mapv + player-position
        (v2/scale (v2/direction player-position world-mouse-position)
                  (min maxrange
                       (v2/distance player-position world-mouse-position)))))

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

(defn- spawn-alert [position faction duration elapsed-time]
  (spawn-effect position
                {:entity/alert-friendlies-after-duration
                 {:counter (timer/create elapsed-time duration)
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
                                       (math/atan2 (v2/crs [0 1] direction)
                                                   (v2/dot [0 1] direction)))]
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

(defn- create-entity-state [[k v] eid elapsed-time]
  (case k
    :active-skill
    (let [[skill effect-ctx] v]
      {:skill skill
       :effect-ctx effect-ctx
       :counter (timer/create elapsed-time
                              (/ (:skill/action-time skill)
                                 (or (stats/get-value (:entity/stats @eid)
                                                      (:skill/action-time-modifier-key skill))
                                     1)))})

    :stunned
    {:counter (timer/create elapsed-time v)}

    :player-moving
    {:movement-vector v}

    :npc-moving
    {:movement-vector v
     :timer (timer/create elapsed-time
                          (* (stats/get-value (:entity/stats @eid) :stats/reaction-time)
                             0.016))}

    :player-item-on-cursor
    {:item v}

    v))

(def ^:private fsms
  {:npc (fsm/fsm-inc
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
             [:player-dead]])})

(defn- create-fsm
  [fsm initial-state]
  (assoc ((case fsm
            :fsms/player (:player fsms)
            :fsms/npc (:npc fsms))
          initial-state
          nil)
         :state initial-state))

(defn- after-create-component
  [elapsed-time eid [k v]]
  (case k
    :entity/fsm
    (let [{:keys [fsm initial-state]} v]
      (swap! eid assoc :entity/fsm (create-fsm fsm initial-state))
      (swap! eid assoc initial-state (create-entity-state [initial-state nil] eid elapsed-time))
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
        (assert (item/valid? item))
        (let [[cell cell-item] (can-pickup-item? (:entity/inventory @eid) item)]
          (assert cell)
          (assert (nil? cell-item))
          (swap! eid set-item cell item)))
      nil)

    nil))

(defn- spawn-entity! [world entity]
  (let [{:world/keys [elapsed-time z-orders minimum-size]} world
        elapsed-time* @elapsed-time
        entity (reduce (fn [m [k v]]
                         (assoc m k (case k
                                      :entity/animation
                                      (create-animation v)

                                      :entity/delete-after-duration
                                      (timer/create elapsed-time* v)

                                      :entity/projectile-collision
                                      (assoc v :already-hit-bodies #{})

                                      :entity/stats
                                      (-> v
                                          (update :stats/mana (fn [v] [v v]))
                                          (update :stats/hp   (fn [v] [v v])))

                                      v)))
                       {}
                       entity)
        entity (let [{:entity/keys [position width height collides? z-order rotation-angle]} entity]
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
                        :entity/rotation-angle (or rotation-angle 0)))
        eid (atom entity)]
    (register-eid! world eid)
    (doseq [component @eid]
      (after-create-component elapsed-time* eid component))
    eid))

(defn- audiovisual! [db schemas world position audiovisual]
  (let [{:keys [entity/animation]} (if (keyword? audiovisual)
                                     (build db schemas audiovisual)
                                     audiovisual)]
    (spawn-entity! @world
                   (spawn-effect position
                                 {:entity/animation (assoc animation :delete-after-stopped? true)}))))

(defn- entity-state-exit!
  [world play-sound! eid world-mouse-position elapsed-time [state-k _state-v]]
  (case state-k
    :player-item-on-cursor
    (let [entity @eid
          item (:entity/item-on-cursor entity)]
      (when item
        (swap! eid dissoc :entity/item-on-cursor)
        (play-sound! "bfxr_itemputground.wav")
        (spawn-entity! @world
                       (spawn-item (item-place-position (:entity/position entity)
                                                        world-mouse-position
                                                        (- (:entity/click-distance-tiles entity) 0.1))
                                   item))))

    :player-moving
    (do (swap! eid dissoc :entity/movement)
        nil)

    :npc-sleeping
    (do (swap! eid add-text-effect elapsed-time "[WHITE]!" 1)
        (spawn-entity! @world
                       (spawn-alert (:entity/position @eid) (:entity/faction @eid) 0.2 elapsed-time)))

    :npc-moving
    (do (swap! eid dissoc :entity/movement)
        nil)

    nil))

(defn- entity-state-enter!
  [play-sound! skin stage eid elapsed-time [state-k state-v]]
  (case state-k
    :player-item-on-cursor
    (let [{:keys [item]} state-v]
      (swap! eid assoc :entity/item-on-cursor item)
      nil)

    :active-skill
    (let [{:keys [skill]} state-v
          cost (:skill/cost skill)]
      (swap! eid update :entity/stats
             (fn [stats]
               (let [mana-val ((stats/get-mana stats) 0)]
                 (assert (<= cost mana-val))
                 (assoc-in stats [:stats/mana 0] (- mana-val cost)))))
      (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?]
             (timer/create elapsed-time (:skill/cooldown skill)))
      (play-sound! (:skill/start-action-sound skill))
      nil)

    :npc-dead
    (do (swap! eid assoc :entity/destroyed? true)
        nil)

    :player-moving
    (let [{:keys [movement-vector]} state-v]
      (swap! eid assoc :entity/movement {:direction movement-vector
                                         :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                    0)})
      nil)

    :player-dead
    (do (play-sound! "bfxr_playerdeath.wav")
        (show-modal! skin stage {:title "YOU DIED - again!"
                                 :text "Good luck next time!"
                                 :button-text "OK"
                                 :on-click (fn [])})
        nil)

    :npc-moving
    (let [{:keys [movement-vector]} state-v]
      (swap! eid assoc :entity/movement {:direction movement-vector
                                         :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                    0)})
      nil)

    nil))

(defn- handle-fsm-event! [world play-sound! skin stage textures eid world-mouse-position event & [params]]
  (let [elapsed-time* @(:world/elapsed-time @world)
        fsm (:entity/fsm @eid)
        _ (assert fsm)
        old-state-k (:state fsm)
        new-fsm (fsm/fsm-event fsm event)
        new-state-k (:state new-fsm)]
    (when-not (= old-state-k new-state-k)
      (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                             [k (k @eid)])
            state-args (if params [new-state-k params] [new-state-k nil])
            new-state-obj [new-state-k (create-entity-state state-args eid elapsed-time*)]]
        (swap! eid assoc :entity/fsm new-fsm)
        (swap! eid assoc new-state-k (new-state-obj 1))
        (swap! eid dissoc old-state-k)
        (entity-state-exit! world play-sound! eid world-mouse-position elapsed-time* old-state-obj)
        (entity-state-enter! play-sound! skin stage eid elapsed-time* new-state-obj)
        nil))))

(defn- effect-applicable?
  [[k v] effect-ctx]
  (case k
    :effects/projectile
    (:effect/target-direction effect-ctx)

    :effects/spawn
    (and (:entity/faction @(:effect/source effect-ctx))
         (:effect/target-position effect-ctx))

    :effects/target-all
    true

    :effects/target-entity
    (and (:effect/target effect-ctx)
         (seq (filter #(effect-applicable? % effect-ctx) (:entity-effects v))))

    :effects.target/convert
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)]
      (and target
           (= (:entity/faction @target)
              (enemy (:entity/faction @source)))))

    :effects.target/damage
    (and (:effect/target effect-ctx)
         #_(:stats/hp @target))

    :effects.target/kill
    (and (:effect/target effect-ctx)
         (:entity/fsm @(:effect/target effect-ctx)))

    :effects.target/melee-damage
    (effect-applicable? [:effects.target/damage (stats/melee-damage @(:effect/source effect-ctx))]
                         effect-ctx)

    :effects.target/spiderweb
    (:entity/stats @(:effect/target effect-ctx))

    :effects.target/stun
    (and (:effect/target effect-ctx)
         (:entity/fsm @(:effect/target effect-ctx)))))

(defn- double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]}
  (let [path-w (+ path-w 0.02)
        v (v2/direction [start-x start-y]
                        [target-y target-y])
        [normal1 normal2] (v2/normal-vectors v)
        normal1 (v2/scale normal1 (/ path-w 2))
        normal2 (v2/scale normal2 (/ path-w 2))
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
           (< (v2/distance source-p target-p)
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
       (filter #(raycaster/line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(def ^:private spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def ^:private spiderweb-duration 5)

(defn- handle-effect!
  [db schemas world play-sound! skin stage textures
   [k v] effect-ctx world-mouse-position apply-effects!
   active-entities raycaster]
  (let [elapsed-time* @(:world/elapsed-time @world)]
    (case k
      :effects/projectile
      (let [source (:effect/source effect-ctx)
            source* @source
            direction (:effect/target-direction effect-ctx)
            size (:projectile/size v)]
        (spawn-entity! @world
                       (spawn-projectile
                        {:position (mapv + (:entity/position source*)
                                         (v2/scale direction
                                                   (+ (/ (:entity/width source*) 2) size 0.1)))
                         :direction direction
                         :faction (:entity/faction source*)}
                        v)))

      :effects/spawn
      (let [source (:effect/source effect-ctx)
            position (:effect/target-position effect-ctx)]
        (play-sound! "bfxr_curse.wav")
        (audiovisual! db schemas world position :audiovisuals/spawn)
        (spawn-entity! @world
                       (spawn-creature {:position position
                                        :creature-property v
                                        :components {:entity/fsm {:fsm :fsms/npc
                                                                  :initial-state :npc-idle}
                                                     :entity/faction (:entity/faction @source)}})))

      :effects/target-all
      (let [source (:effect/source effect-ctx)
            source* @source]
        (doseq [target (affected-targets active-entities raycaster source*)]
          (spawn-entity! @world
                         (spawn-line
                          {:start (:entity/position source*)
                           :end (:entity/position @target)
                           :duration 0.05
                           :color (float-bits 1 0 0 0.75)
                           :thick? true}))
          (apply-effects! {:effect/source source
                           :effect/target target}
                          (:entity-effects v))))

      :effects/target-entity
      (let [source (:effect/source effect-ctx)
            target (:effect/target effect-ctx)
            body        @source
            target-body @target
            {:keys [maxrange entity-effects]} v]
        (if (in-range? body target-body maxrange)
          (do (spawn-entity! @world
                             (spawn-line
                              {:start (start-point body target-body)
                               :end (:entity/position target-body)
                               :duration 0.05
                               :color (float-bits 1 0 0 0.75)
                               :thick? true}))
              (apply-effects! effect-ctx entity-effects))
          (do (play-sound! "bfxr_fisthit.wav")
              (audiovisual! db schemas world
                            (end-point body target-body maxrange)
                            :audiovisuals/hit-ground))))

      :effects.target/convert
      (let [source (:effect/source effect-ctx)
            target (:effect/target effect-ctx)]
        (play-sound! "bfxr_burrow.wav")
        (audiovisual! db schemas world (:entity/position @target) :audiovisuals/convert)
        (swap! target assoc :entity/faction (:entity/faction @source))
        nil)

      :effects.target/damage
      (let [source (:effect/source effect-ctx)
            target (:effect/target effect-ctx)
            source* @source
            target* @target
            hp (stats/get-hitpoints (:entity/stats target*))]
        (cond
         (zero? (hp 0))
         nil

         ; TODO find a better way
         (not (:entity/stats target*))
         nil

         (and (:entity/stats source*)
              (:entity/stats target*)
              (< (rand) (stats/effective-armor-save (:entity/stats source*)
                                                    (:entity/stats target*))))
         (do (swap! target add-text-effect elapsed-time* "[WHITE]ARMOR" 0.3)
             nil)

         :else
         (let [min-max (if (:entity/stats source*)  ; projectiles dont have ....
                         (:damage/min-max (stats/calc-damage (:entity/stats source*)
                                                             (:entity/stats target*)
                                                             v))
                         (:damage/min-max v))
               dmg-amount (let [[lower upper] min-max]
                            (+ lower (rand-int (inc (- upper lower)))))

               new-hp-val (max (- (hp 0) dmg-amount)
                               0)
               dmg-text (str "[RED]" dmg-amount "[]")]
           (swap! target assoc-in [:entity/stats :stats/hp 0] new-hp-val)
           (swap! target add-text-effect elapsed-time* dmg-text 0.3)
           (handle-fsm-event! world play-sound! skin stage textures target world-mouse-position (if (zero? new-hp-val) :kill :alert))
           (play-sound! "bfxr_normalhit.wav")
           (audiovisual! db schemas world (:entity/position target*) :audiovisuals/damage))))

      :effects.target/kill
      (handle-fsm-event! world play-sound! skin stage textures (:effect/target effect-ctx) world-mouse-position :kill)

      :effects.target/melee-damage
      ; TODO AT EFFECT CREATION MAKE
      ; same @ applicable
      (handle-effect! db schemas world play-sound! skin stage textures
                      [:effects.target/damage (stats/melee-damage @(:effect/source effect-ctx))]
                      effect-ctx
                      world-mouse-position
                      apply-effects!
                      active-entities
                      raycaster)

      :effects.target/spiderweb
      (let [target (:effect/target effect-ctx)]
        ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
        (when-not (:entity/temp-modifier @target)
          (swap! target assoc :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                     :counter (timer/create elapsed-time* spiderweb-duration)})
          (swap! target update :entity/stats stats/add-mods spiderweb-modifiers)
          nil))

      :effects.target/stun
      (handle-fsm-event! world play-sound! skin stage textures (:effect/target effect-ctx) world-mouse-position :stun v))))

(defn- usable-state
  [{:keys [skill/cooling-down? skill/effects] :as skill}
   entity
   effect-ctx]
  (cond
   cooling-down?
   :cooldown

   (> (:skill/cost skill) ((stats/get-mana (:entity/stats entity)) 0))
   :not-enough-mana

   (not (seq (filter #(effect-applicable? % effect-ctx) effects)))
   :invalid-params

   :else
   :usable))

(defn create-skin! [^FileHandle file-handle]
  (let [s (Skin. file-handle)]
    (set! (.markupEnabled ^BitmapFont$BitmapFontData
                          (.getData (.getFont ^Skin s "default-font")))
          true)
    s))

(defn create-cursors! [files]
  (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap* (Pixmap. ^FileHandle (.internal ^Files files path))
                         cursor (.newCursor Gdx/graphics ^Pixmap pixmap* hotspot-x hotspot-y)]
                     (Disposable/.dispose pixmap*)
                     cursor)))))

(defn create-viewport! [world-unit-scale]
  (let [world-width (* 1440 world-unit-scale)
        world-height (* 900 world-unit-scale)]
    (FitViewport. (float world-width)
                  (float world-height)
                  (doto (OrthographicCamera.)
                    (.setToOrtho false world-width world-height)))))

(defn create-default-font! [files {:keys [path
                                          size
                                          quality-scaling
                                          use-integer-positions?]}]
  (let [generator (FreeTypeFontGenerator. ^FileHandle (.internal ^Files files path))
        parameter (let [p (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                    (set! (.size p) (* size quality-scaling))
                    (set! (.minFilter p) Texture$TextureFilter/Linear)
                    (set! (.magFilter p) Texture$TextureFilter/Linear)
                    p)
        font (.generateFont ^FreeTypeFontGenerator generator
                            ^FreeTypeFontGenerator$FreeTypeFontParameter parameter)
        font-data (.getData ^BitmapFont font)]
    (Disposable/.dispose generator)
    (.setScale ^BitmapFont$BitmapFontData font-data (/ quality-scaling))
    (set! (.markupEnabled ^BitmapFont$BitmapFontData font-data) true)
    (.setUseIntegerPositions ^BitmapFont font use-integer-positions?)
    font))

(defn create-level! [level-fn db* textures*]
  (let [{level-tiled-map :tiled-map
         level-start :start-position}
        (level-fn {:level/creature-properties (tiled-map/prepare-creature-tiles
                                               (all-raw db* :properties/creatures)
                                               #(textures/texture-region textures* %))
                   :textures textures*})]
    {:tiled-map level-tiled-map
     :start-position level-start}))

(defn spawn-player! [db* schemas world skin stage textures*]
  (let [eid (spawn-entity! @world (spawn-creature {:position (mapv (partial + 0.5) (:world/start-position @world))
                                            :creature-property (build db* schemas :creatures/vampire)
                                            :components {:entity/fsm {:fsm :fsms/player
                                                                      :initial-state :player-idle}
                                                         :entity/faction :good
                                                         :entity/player? true
                                                         :entity/free-skill-points 3
                                                         :entity/clickable {:type :clickable/player}
                                                         :entity/click-distance-tiles 1.5}}))]
    (sync-player-ui! skin stage textures* @(:world/elapsed-time @world) eid)))

(defn bind-player-eid! [world]
  (let [eid (get @(:world/entity-ids @world) 1)]
    (assert (:entity/player? @eid))
    (swap! world assoc :world/player-eid eid)))

(defn spawn-map-creatures! [db* schemas world]
  (let [world* @world
        sp (:world/start-position world*)]
    (doseq [[position creature-id] (tiled-map/spawn-positions (:world/tiled-map world*))
            :when (not= position sp)]
      (spawn-entity! @world (spawn-creature {:position (mapv (partial + 0.5) position)
                                      :creature-property (build db* schemas (keyword creature-id))
                                      :components {:entity/fsm {:fsm :fsms/npc
                                                                :initial-state :npc-sleeping}
                                                   :entity/faction :evil}})))))


(defn hp-mana-bar-create
  [default-font stage textures unit-scale world]
  (let [{:keys [rahmen-file
                rahmenw
                rahmenh
                hpcontent-file
                manacontent-file
                y-mana]} {:rahmen-file "images/rahmen.png"
                          :rahmenw 150
                          :rahmenh 26
                          :hpcontent-file "images/hp.png"
                          :manacontent-file "images/mana.png"
                          :y-mana 80}
        [x y-mana] [(/ (.getWorldWidth (.getViewport ^Stage stage)) 2)
                    y-mana]
        rahmen-tex-reg (textures/texture-region textures {:image/file rahmen-file})
        y-hp (+ y-mana rahmenh)
        draw-hpmana-bar! (fn [ctx batch x y content-file minmaxval name]
                           (draw-fn-texture-region batch unit-scale rahmen-tex-reg [x y])
                           (draw-fn-texture-region batch unit-scale
                                                   (textures/texture-region textures
                                                                            {:image/file content-file
                                                                             :image/bounds [0 0 (* rahmenw (val-max/ratio minmaxval)) rahmenh]})
                                                   [x y])
                           (draw-fn-text batch default-font unit-scale {:text (str (number/readable (minmaxval 0))
                                                         "/"
                                                         (minmaxval 1)
                                                         " "
                                                         name)
                                              :x (+ x 75)
                                              :y (+ y 2)
                                              :up? true}))]
    (proxy [Actor] []
      (act [delta]
        (let [^Actor this this]
          (proxy-super act delta)))
      (draw [batch parent-alpha]
        (when (.getStage ^Actor this)
          (let [stats (:entity/stats @(:world/player-eid @world))
                bar-x (- x (/ rahmenw 2))]
            (draw-hpmana-bar! nil batch bar-x y-hp hpcontent-file (stats/get-hitpoints stats) "HP")
            (draw-hpmana-bar! nil batch bar-x y-mana manacontent-file (stats/get-mana stats) "MP")))))))

(defn handle-clicked-inventory-cell
  [world player-eid play-sound! skin stage textures set-item! remove-item! cell world-mouse-position]
  (case (:state (:entity/fsm @player-eid))
    :player-idle
    (when-let [item (item-at (:entity/inventory @player-eid) cell)]
      (play-sound! "bfxr_takeit.wav")
      (swap! player-eid remove-item cell)
      (remove-item! cell)
      (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :pickup-item item))

    :player-item-on-cursor
    (let [entity @player-eid
          inventory (:entity/inventory entity)
          item-in-cell (item-at inventory cell)
          item-on-cursor (:entity/item-on-cursor entity)]
      (cond
       (and (not item-in-cell)
            (valid-slot? cell item-on-cursor))
       (do (swap! player-eid dissoc :entity/item-on-cursor)
           (play-sound! "bfxr_itemput.wav")
           (swap! player-eid set-item cell item-on-cursor)
           (set-item! cell item-on-cursor)
           (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :dropped-item))

       (and item-in-cell
            (valid-slot? cell item-on-cursor))
       (do (swap! player-eid dissoc :entity/item-on-cursor)
           (play-sound! "bfxr_itemput.wav")
           (swap! player-eid remove-item cell)
           (remove-item! cell)
           (swap! player-eid set-item cell item-on-cursor)
           (set-item! cell item-on-cursor)
           (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :dropped-item)
           (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :pickup-item item-in-cell))))

    nil))

(defn player-message-actor-create [default-font unit-scale]
  (let [message-duration-seconds 0.5]
    (doto (proxy [Actor] []
            (act [delta]
              (let [state (.getUserObject ^Actor this)]
                (when (:text @state)
                  (swap! state update :counter + delta)
                  (when (>= (:counter @state) message-duration-seconds)
                    (reset! state nil))))
              (let [^Actor this this]
                (proxy-super act delta)))
            (draw [batch parent-alpha]
              (when-let [stage (.getStage ^Actor this)]
                (let [state (.getUserObject ^Actor this)
                      vp-width (.getWorldWidth (.getViewport ^Stage stage))
                      vp-height (.getWorldHeight (.getViewport ^Stage stage))]
                  (when-let [text (:text @state)]
                    (draw-fn-text batch default-font unit-scale {:x (/ vp-width 2)
                                       :y (+ (/ vp-height 2) 200)
                                       :text text
                                       :scale 2.5
                                       :up? true}))))))
      (.setName "player-message")
      (.setUserObject (atom nil)))))

(defn- entity-state-draw-ui-view
  [[k _v] eid batch unit-scale textures mouseover-actor* ui-mouse-pos]
  (case k
    :player-item-on-cursor
    (when mouseover-actor*
      (draw-fn-texture-region batch unit-scale
                              (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
                              ui-mouse-pos
                              {:center? true}))

    nil))

(defn player-state-draw-create [unit-scale textures world]
  (proxy [Actor] []
    (act [delta]
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (let [stage (.getStage ^Actor this)
            player-eid (:world/player-eid @world)
            entity @player-eid
            state-k (:state (:entity/fsm entity))
            mouse-pos (ui-mouse-position stage)
            [x y] mouse-pos]
        (entity-state-draw-ui-view [state-k (state-k entity)]
                                   player-eid
                                   batch
                                   unit-scale
                                   textures
                                   (mouseover-actor stage x y)
                                   mouse-pos)))))

(defn windows-create [actor-fns]
  (let [group* (Group.)]
    (run! #(.addActor ^Group group* ^Actor %) (for [f actor-fns] (f)))
    (doto group*
      (.setName "moon.ui.windows"))))

(defn draw-cell-debug
  [ctx shape-drawer ^Viewport viewport world
   show-cell-entities? show-cell-occupied? show-potential-field-colors?
   factions-iterations]
  (let [world @world
        ^OrthographicCamera camera (.getCamera viewport)
        plane-points (mapv (fn [^Vector3 v3]
                             [(.x v3) (.y v3) (.z v3)])
                           (.planePoints (.frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))
        tile-positions (for [x (range (int left-x) (int right-x))
                             y (range (int bottom-y) (+ 2 (int top-y)))]
                         [x y])]
    (doseq [[[x y] cell*] (cells-at world tile-positions)]
      (when (and @show-cell-entities? (seq (:entities cell*)))
        (shape-drawer/filled-rectangle! shape-drawer x y 1 1 (float-bits 1 0 0 0.6)))
      (when (and @show-cell-occupied? (seq (:occupied cell*)))
        (shape-drawer/filled-rectangle! shape-drawer x y 1 1 (float-bits 0 0 1 0.6)))
      (when-let [faction @show-potential-field-colors?]
        (let [{:keys [distance]} (faction cell*)]
          (when distance
            (let [ratio (/ distance (factions-iterations faction))]
              (shape-drawer/filled-rectangle! shape-drawer x y 1 1 (float-bits ratio (- 1 ratio) ratio 0.6)))))))))

(defn- draw-fn-grid [shape-drawer leftx bottomy gridw gridh cellw cellh color-float-bits]
  (let [w (* (float gridw) (float cellw))
        h (* (float gridh) (float cellh))
        topy (+ (float bottomy) (float h))
        rightx (+ (float leftx) (float w))]
    (doseq [idx (range (inc (float gridw)))
            :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
      (shape-drawer/line! shape-drawer [linex topy] [linex bottomy] color-float-bits))
    (doseq [idx (range (inc (float gridh)))
            :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
      (shape-drawer/line! shape-drawer [leftx liney] [rightx liney] color-float-bits))))

(defn draw-tile-grid
  [ctx shape-drawer ^Viewport viewport show-tile-grid?]
  (when @show-tile-grid?
    (let [^OrthographicCamera camera (.getCamera viewport)
          plane-points (mapv (fn [^Vector3 v3]
                               [(.x v3) (.y v3) (.z v3)])
                             (.planePoints (.frustum camera)))
          frustum-points (take 4 plane-points)
          left-x   (apply min (map first  frustum-points))
          bottom-y (apply min (map second frustum-points))]
      (draw-fn-grid shape-drawer
                    (int left-x)
                    (int bottom-y)
                    (inc (int (.getWorldWidth viewport)))
                    (+ 2 (int (.getWorldHeight viewport)))
                    1
                    1
                    (float-bits 1 1 1 0.8)))))

(defn highlight-mouseover-tile
  [ctx shape-drawer world-mouse-position world]
  (let [world @world
        [x y] (mapv int world-mouse-position)
        cell (cell-at world [x y])]
    (when (and cell (#{:air :none} (:movement cell)))
      (shape-drawer/rectangle! shape-drawer x y 1 1
                         (case (:movement cell)
                           :air (float-bits 1 1 0 0.5)
                           :none (float-bits 1 0 0 0.5))))))

(def ^:private active-skill-radius
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- effect-render
  [[k v] effect-ctx shape-drawer active-entities raycaster]
  (case k
    :effects/target-all
    (let [source (:effect/source effect-ctx)
          source* @source]
      (doseq [target* (map deref (affected-targets active-entities raycaster source*))]
        (shape-drawer/line! shape-drawer
                      (:entity/position source*)
                      (:entity/position target*)
                      (float-bits 1 0 0 0.5))))

    :effects/target-entity
    (when-let [target (:effect/target effect-ctx)]
      (let [source (:effect/source effect-ctx)
            body        @source
            target-body @target
            maxrange (:maxrange v)]
        (shape-drawer/line! shape-drawer
                      (start-point body target-body)
                      (end-point body target-body maxrange)
                      (if (in-range? body target-body maxrange)
                        (float-bits 1 0 0 0.5)
                        (float-bits 1 1 0 0.5)))))

    nil))

(defn- draw-component
  [shape-drawer batch default-font unit-scale world-unit-scale mouseover-actor world-mouse-position
   textures player elapsed-time active-entities raycaster
   entity k v]
  (case k
    :entity/clickable
    (let [{:keys [text]} v
          {:keys [entity/position entity/height entity/mouseover?]} entity]
      (when (and mouseover? text)
        (let [[x y] position]
          (draw-fn-text batch default-font unit-scale {:text text
                             :x x
                             :y (+ y (/ height 2))
                             :up? true}))))

    :player-item-on-cursor
    (let [{:keys [item]} v]
      (when-not mouseover-actor
        (draw-fn-texture-region batch unit-scale
                                (textures/texture-region textures (:entity/image item))
                                (item-place-position (:entity/position entity)
                                                     world-mouse-position
                                                     (- (:entity/click-distance-tiles entity) 0.1))
                                {:center? true})))

    :entity/animation
    (let [{:keys [frames cnt frame-duration]} v
          image (frames (min (int (/ (float cnt) (float frame-duration)))
                             (dec (count frames))))]
      (draw-fn-texture-region batch unit-scale
                              (textures/texture-region textures image)
                              (:entity/position entity)
                              {:center? true
                               :rotation (or (:entity/rotation-angle entity) 0)}))

    :entity/image
    (draw-fn-texture-region batch unit-scale
                            (textures/texture-region textures v)
                            (:entity/position entity)
                            {:center? true
                             :rotation (or (:entity/rotation-angle entity) 0)})

    :entity/line-render
    (let [{:keys [thick? end color]} v
          position (:entity/position entity)]
      (if thick?
        (shape-drawer/with-line-width shape-drawer 4
          (shape-drawer/line! shape-drawer position end color))
        (shape-drawer/line! shape-drawer position end color)))

    :entity/mouseover?
    (let [{:keys [entity/position entity/width entity/height entity/faction]} entity
          color (cond (= faction (enemy (:entity/faction player)))
                      (float-bits 1 0 0 0.4)
                      (= faction (:entity/faction player))
                      (float-bits 0 1 0 0.4)
                      :else
                      (float-bits 1 1 1 0.4))]
      (shape-drawer/with-line-width shape-drawer 5
        (shape-drawer/ellipse! shape-drawer position
                               (/ width 2)
                               (/ height 2)
                               color)))

    :entity/stats
    (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
      (when (or (< ratio 1) (:entity/mouseover? entity))
        (let [{:keys [entity/position entity/width entity/height]} entity
              [x y] position
              x (- x (/ width  2))
              y (+ y (/ height 2))
              height (* 5 world-unit-scale)
              border (* 1 world-unit-scale)]
          (shape-drawer/filled-rectangle! shape-drawer x y width height (float-bits 0 0 0 1))
          (shape-drawer/filled-rectangle! shape-drawer
                                    (+ x border)
                                    (+ y border)
                                    (- (* width ratio) (* 2 border))
                                    (- height (* 2 border))
                                    (cond
                                      (> ratio 0.75) (float-bits 0 0.8 0 1)
                                      (> ratio 0.5) (float-bits 0 0.5 0 1)
                                      (> ratio 0.25) (float-bits 0.5 0.5 0 1)
                                      :else (float-bits 0.5 0 0 1))))))

    :entity/string-effect
    (let [{:keys [text]} v
          [x y] (:entity/position entity)]
      (draw-fn-text batch default-font unit-scale {:text text
                         :x x
                         :y (+ y
                               (/ (:entity/height entity) 2)
                               (* 5 world-unit-scale))
                         :scale 2
                         :up? true}))

    :entity/temp-modifier
    (shape-drawer/filled-circle! shape-drawer (:entity/position entity) 0.5 (float-bits 0.5 0.5 0.5 0.4))

    :active-skill
    (let [{:keys [skill effect-ctx counter]} v
          {:keys [entity/image skill/effects]} skill
          radius active-skill-radius
          action-counter-ratio (timer/ratio elapsed-time counter)
          texture-region (textures/texture-region textures image)
          [x y] (:entity/position entity)
          y (+ (float y)
               (float (/ (:entity/height entity) 2))
               (float 0.15))
          center [x (+ y radius)]]
      (shape-drawer/filled-circle! shape-drawer center radius (float-bits 1 1 1 0.125))
      (shape-drawer/sector! shape-drawer
                      center
                      radius
                      (math/to-radians 90)
                      (math/to-radians (* (float action-counter-ratio) 360))
                      (float-bits 1 1 1 0.5))
      (draw-fn-texture-region batch unit-scale texture-region [(- (float x) radius) y])
      (doseq [effect effects]
        (effect-render effect effect-ctx shape-drawer
                       active-entities
                       raycaster)))

    :npc-sleeping
    (let [{:keys [entity/position entity/height]} entity
          [x y] position]
      (draw-fn-text batch default-font unit-scale {:text "zzz"
                         :x x
                         :y (+ y (/ height 2))
                         :up? true}))

    :stunned
    (shape-drawer/circle! shape-drawer (:entity/position entity) 0.5 (float-bits 1 1 1 0.6))))

(def ^:private render-layers
  [#{:entity/mouseover?
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
     :active-skill}])

(defn- draw-entity-rectangle!
  [ctx shape-drawer entity color-float-bits]
  (let [{:keys [entity/position entity/width entity/height]} entity
        [x y] [(- (position 0) (/ width 2))
               (- (position 1) (/ height 2))]]
    (shape-drawer/rectangle! shape-drawer x y width height color-float-bits)))

(defn draw-entities!
  [ctx shape-drawer batch default-font textures unit-scale world-unit-scale mouseover-actor world-mouse-position
   raycaster world show-body-bounds? active-entities render-z-order]
  (let [player-eid (:world/player-eid @world)
        elapsed-time @(:world/elapsed-time @world)
        show-body-bounds? @show-body-bounds?
        active-entities @active-entities
        entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (sort-by (comp render-z-order key) <
                                        (group-by :entity/z-order entities))
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (when show-body-bounds?
        (draw-entity-rectangle! ctx shape-drawer
                                entity
                                (if (:entity/collides? entity)
                                  (float-bits 1 1 1 1)
                                  (float-bits 0.5 0.5 0.5 1))))
      (doseq [[k v] entity
              :when (get render-layer k)]
        (draw-component shape-drawer batch default-font unit-scale world-unit-scale mouseover-actor world-mouse-position
                        textures player elapsed-time active-entities raycaster
                        entity k v)))))

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

(defn- create-effect-ctx
  [world ctx eid]
  (let [world @world
        entity @eid
        target (nearest-enemy (:world/grid world) entity)
        target (when (and target
                          (raycaster/line-of-sight? (:world/raycaster world) entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (direction entity
                                                @target))}))

(defn- update-effect-ctx
  [raycaster effect-ctx]
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)]
    (if (and target
             (not (:entity/destroyed? @target))
             (raycaster/line-of-sight? raycaster @source @target))
      effect-ctx
      (dissoc effect-ctx :effect/target))))

(defn outer-rectangle
  [{[x y] :position :keys [radius]}]
  (let [radius (float radius)
        size (* radius 2)]
    {:x (- (float x) radius)
     :y (- (float y) radius)
     :width  size
     :height size}))

(defn tick-component
  [world delta-time play-sound! skin stage textures
   max-speed
   ctx world-mouse-position apply-effects! eid [k v]]
  (let [elapsed-time* @(:world/elapsed-time @world)]
    (case k
      :entity/animation
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
        nil)

      :entity/alert-friendlies-after-duration
      (let [{:keys [counter faction]} v]
        (when (timer/stopped? elapsed-time* counter)
          (swap! eid assoc :entity/destroyed? true)
          (doseq [friendly-eid (let [circle {:position (:entity/position @eid)
                                             :radius 4}
                                     [x y] (:position circle)
                                     g2d (:world/grid @world)
                                     gdx-circle (Circle. (float x) (float y) (float (:radius circle)))]
                                 (->> circle
                                      outer-rectangle
                                      rectangle/touched-tiles
                                      (keep #(get-in g2d %))
                                      (map deref)
                                      (into #{} (mapcat :entities))
                                      (filter #(Intersector/overlaps ^Circle gdx-circle
                                                                     ^Rectangle (rectangle @%)))
                                      (filter #(= (:entity/faction @%) faction))))]
            (handle-fsm-event! world play-sound! skin stage textures friendly-eid world-mouse-position :alert)))
        nil)

      :entity/string-effect
      (let [{:keys [counter]} v]
        (when (timer/stopped? elapsed-time* counter)
          (swap! eid dissoc :entity/string-effect))
        nil)

      :entity/skills
      (do (doseq [{:keys [skill/cooling-down?] :as skill} (vals v)
                  :when (and cooling-down?
                             (timer/stopped? elapsed-time* cooling-down?))]
            (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] false))
          nil)

      :entity/temp-modifier
      (let [{:keys [modifiers counter]} v]
        (when (timer/stopped? elapsed-time* counter)
          (swap! eid dissoc :entity/temp-modifier)
          (swap! eid update :entity/stats stats/remove-mods modifiers))
        nil)

      :entity/projectile-collision
      (let [{:keys [entity-effects already-hit-bodies piercing?]} v
            world* @world
            entity @eid
            touched-cells (map deref (keep #(get-in (:world/grid world*) %) (touched-tiles entity)))
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
          (apply-effects! {:effect/source eid
                           :effect/target hit-entity}
                          entity-effects))
        nil)

      :active-skill
      (let [{:keys [skill effect-ctx counter]} v
            effect-ctx (update-effect-ctx (:world/raycaster @world) effect-ctx)]
        (cond
         (not (seq (filter #(effect-applicable? % effect-ctx)
                           (:skill/effects skill))))
         (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :action-done)

         (timer/stopped? elapsed-time* counter)
         (do (apply-effects! effect-ctx (:skill/effects skill))
             (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :action-done)
             nil)))

      :entity/delete-after-duration
      (do (when (timer/stopped? elapsed-time* v)
            (swap! eid assoc :entity/destroyed? true))
          nil)

      :stunned
      (let [{:keys [counter]} v]
        (when (timer/stopped? elapsed-time* counter)
          (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :effect-wears-off)))

      :npc-moving
      (let [{:keys [timer]} v]
        (when (timer/stopped? elapsed-time* timer)
          (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :timer-finished)))

      :npc-sleeping
      (let [entity @eid]
        (when-let [distance (nearest-enemy-distance (:world/grid @world) entity)]
          (when (<= distance (stats/get-value (:entity/stats entity) :stats/aggro-range))
            (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :alert))))

      :npc-idle
      (let [raycaster (:world/raycaster @world)
            effect-ctx (create-effect-ctx world ctx eid)]
        (if-let [skill (choose-skill (partial raycaster/blocked? raycaster) @eid effect-ctx)]
          (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :start-action [skill effect-ctx])
          (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :movement-direction (or (find-direction (:world/grid @world) eid)
                                                             [0 0]))))

      :entity/movement
      (let [{:keys [direction
                    speed]
             :as movement} v]
        (assert (<= 0 speed max-speed)
                (pr-str speed))
        (assert (vector? direction))
        (assert (or (zero? (v2/length direction))
                    (number/nearly-equal? 1 (v2/length direction)))
                (str "cannot understand direction: " (pr-str direction)))
        (when-not (or (zero? (v2/length direction))
                      (nil? speed)
                      (zero? speed))
          (let [world* @world
                movement (assoc movement :delta-time delta-time)
                body @eid]
            (when-let [body (if (:entity/collides? body)
                               (try-move-solid-body (:world/grid world*) body (:entity/id @eid) movement)
                               (let [{:keys [direction speed delta-time]} movement]
                                 (update body :entity/position
                                         #(mapv (fn [p d] (+ p (* d speed delta-time))) % direction))))]
              (swap! eid assoc :entity/position (:entity/position body))
              (relocate-eid! world* eid)
              nil))))

      nil)))

(def ^:private help-menu-item
  {:label "Help"
   :items [{:label controls-info}]})

(defn- debug-flags-menu-item
  [show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?]
  {:label "Debug"
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
            :on-click #(reset! show-potential-field-colors? :evil)}]})

(defn- dev-menus
  [show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?]
  [(debug-flags-menu-item show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?)
   help-menu-item
   ])

(defn create-ui-actors
  [world mouseover-eid paused?
   show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?
   unit-scale
   play-sound! default-font shape-drawer skin stage textures* viewport]
  (let [cell-size 48]
    [(action-bar/create)
     (dev-menu/create
      {:menus (dev-menus show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?)
       :update-labels (for [item [{:label "elapsed-time"
                                   :update-fn (fn []
                                                (str (number/readable @(:world/elapsed-time @world)) " seconds"))
                                   :icon "images/clock.png"}
                                  {:label "FPS"
                                   :update-fn (fn [] (.getFramesPerSecond Gdx/graphics))
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
                                                (mapv int (ui-mouse-position stage)))}
                                  {:label "World"
                                   :update-fn (fn []
                                                (mapv int (world-mouse-position viewport)))}
                                  {:label "Zoom"
                                   :update-fn (fn []
                                                (.zoom ^OrthographicCamera (.getCamera ^Viewport viewport)))
                                   :icon "images/zoom.png"}]]
                        (if (:icon item)
                          (update item :icon #(get textures* %))
                          item))
       :skin skin})
     (hp-mana-bar-create default-font stage textures* unit-scale world)
     (windows-create [#(info-window-create skin stage mouseover-eid world)
                      #(create-inventory-window
                        (fn [_event cell]
                          (handle-clicked-inventory-cell world
                                                         (:world/player-eid @world)
                                                         play-sound!
                                                         skin
                                                         stage
                                                         textures*
                                                         (fn [cell item] (set-item! skin stage textures* cell item))
                                                         (fn [cell] (remove-item! stage cell))
                                                         cell
                                                         (world-mouse-position viewport)))
                        (fn [player-entity x y mouseover? cell]
                          (shape-drawer/rectangle! shape-drawer x y cell-size cell-size (float-bits 0.5 0.5 0.5 1))
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (valid-slot? cell item)
                                          (float-bits 0 0.6 0 0.8)
                                          (float-bits 0.6 0 0 0.8))]
                              (shape-drawer/filled-rectangle! shape-drawer (inc x) (inc y) (- cell-size 2) (- cell-size 2) color))))
                        skin
                        stage
                        textures*
                        world)])
     (player-state-draw-create unit-scale textures* world)
     (player-message-actor-create default-font unit-scale)]))

(defn update-mouseover-eid! [world mouseover-eid stage viewport render-z-order]
  (let [old-mouseover-eid @mouseover-eid
        [x y] (ui-mouse-position stage)
        new-eid (if (mouseover-actor stage x y)
                  nil
                  (let [player @(:world/player-eid @world)
                        world* @world
                        hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                     (point->entities (:world/grid world*) (world-mouse-position viewport)))]
                    (->> hits
                         (sort-by #((:entity/z-order @%) render-z-order) <)
                         reverse
                         (filter #(raycaster/line-of-sight? (:world/raycaster world*) player @%))
                         first)))]
    (when old-mouseover-eid
      (swap! old-mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (reset! mouseover-eid new-eid)))

(defn update-active-entities! [world active-entities]
  (reset! active-entities (active-entities* (:world/content-grid @world) @(:world/player-eid @world))))

(defn set-camera-to-player! [world viewport]
  (let [^OrthographicCamera camera (.getCamera ^Viewport viewport)
        pos (.position camera)
        [x y] (:entity/position @(:world/player-eid @world))]
    (set! (.x pos) x)
    (set! (.y pos) y)
    (.update camera)))

(defn current-mouseover-actor [stage]
  (let [[x y] (ui-mouse-position stage)]
    (mouseover-actor stage x y)))

(defn- cursor-key [interaction-state world]
  (let [eid (:world/player-eid @world)
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

(defn destroy-entities! [db schemas world play-sound!]
  (doseq [eid (filter (comp :entity/destroyed? deref) (vals @(:world/entity-ids @world)))]
    (unregister-eid! @world eid)
    (doseq [[k v] @eid]
      (case k
        :entity/destroy-audiovisual
        (do (case v
              :audiovisuals/creature-die (play-sound! "bfxr_defaultmonsterdeath.wav")
              :audiovisuals/hit-wall (play-sound! "bfxr_projectile_wallhit.wav"))
            (audiovisual! db schemas world (:entity/position @eid) v))
        nil))))

(defn- movement-vector [key-pressed?]
  (let [r (when (key-pressed? Input$Keys/D) [1  0])
        l (when (key-pressed? Input$Keys/A) [-1 0])
        u (when (key-pressed? Input$Keys/W) [0  1])
        d (when (key-pressed? Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v2/normalise (reduce #(mapv + %1 %2) [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2/length v))
          v)))))

(defn- show-message! [stage message]
  (-> (.getRoot ^Stage stage)
      (.findActor "player-message")
      (.setUserObject (atom {:text message :counter 0}))))

(defn- interaction-state->txs [[k params] world stage play-sound! skin textures set-item! player-eid world-mouse-position]
  (case k
    :interaction-state/mouseover-actor
    nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          (do (toggle-inventory-visible! stage)
              nil)

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
              (-> (.getRoot ^Stage stage)
                  (.findActor "moon.ui.windows.inventory")
                  .isVisible)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! "bfxr_takeit.wav")
                  (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :pickup-item item))

              (can-pickup-item? (:entity/inventory @player-eid) item)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! "bfxr_pickup.wav")
                  (assert (item/valid? item))
                  (let [[cell cell-item] (can-pickup-item? (:entity/inventory @player-eid) item)]
                    (assert cell)
                    (assert (nil? cell-item))
                    (swap! player-eid set-item cell item)
                    (set-item! cell item))
                  nil)

              :else
              (do (play-sound! "bfxr_denied.wav")
                  (show-message! stage "Your Inventory is full")
                  nil))))
        (do (play-sound! "bfxr_denied.wav")
            (show-message! stage "Too far away")
            nil)))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :start-action [skill effect-ctx]))

    :interaction-state.skill/not-usable
    (let [state params]
      (do (play-sound! "bfxr_denied.wav")
          (show-message! stage (case state
                                 :cooldown "Skill is still on cooldown"
                                 :not-enough-mana "Not enough mana"
                                 :invalid-params "Cannot use this here"))
          nil))

    :interaction-state/no-skill-selected
    (do (play-sound! "bfxr_denied.wav")
        (show-message! stage "No selected skill")
        nil)))

(defn handle-player-input!
  [world interaction-state
   play-sound! skin stage textures viewport key-pressed? button-just-pressed?]
  (let [eid (:world/player-eid @world)
        entity @eid
        state-k (:state (:entity/fsm entity))
        left-button-pressed? (button-just-pressed? Input$Buttons/LEFT)
        move-v (movement-vector key-pressed?)
        [x y] (ui-mouse-position stage)
        mouseover-actor* (mouseover-actor stage x y)
        world-mouse-pos (world-mouse-position viewport)]
    (case state-k
      :player-idle
      (if move-v
        (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-pos :movement-input move-v)
        (when left-button-pressed?
          (interaction-state->txs interaction-state
                                  world
                                  stage
                                  play-sound!
                                  skin
                                  textures
                                  #(set-item! skin stage textures %1 %2)
                                  eid
                                  world-mouse-pos)))

      :player-moving
      (if move-v
        (do (swap! eid assoc :entity/movement {:direction move-v
                                               :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                          0)})
            nil)
        (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-pos :no-movement-input))

      :player-item-on-cursor
      (when (and left-button-pressed?
                 (not mouseover-actor*))
        (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-pos :drop-item))

      nil)))

(defn- tick-entities!
  [db schemas world delta-time active-entities
   play-sound! skin stage textures max-speed viewport]
  (let [active-entities* @active-entities
        raycaster (:world/raycaster @world)
        world-mouse-pos (world-mouse-position viewport)]
    (letfn [(apply-effects! [effect-ctx effects]
              (doseq [e (filter #(effect-applicable? % effect-ctx) effects)]
                (handle-effect! db schemas world play-sound! skin stage textures e effect-ctx world-mouse-pos
                               apply-effects!
                               active-entities*
                               raycaster)))]
      (doseq [eid active-entities*
              component @eid]
        (tick-component world delta-time play-sound! skin stage textures
                        max-speed
                        nil world-mouse-pos
                        apply-effects!
                        eid component)))))

(defn tick-game!
  [db schemas world active-entities paused?
   play-sound! skin stage textures viewport factions-iterations
   max-delta max-speed]
  (when-not @paused?
    (let [delta-time (min (.getDeltaTime Gdx/graphics) max-delta)
          world* @world]
      (swap! (:world/elapsed-time world*) + delta-time)
      (doseq [[faction max-iterations] factions-iterations]
        (update! (:world/grid world*)
                 (:world/potential-field-cache world*)
                 faction
                 @active-entities
                 max-iterations))
      (tick-entities! db schemas world delta-time active-entities
                      play-sound! skin stage textures max-speed viewport))))

(defn- create!
  [{:keys [audio sound-paths batch unit-scale shape-drawer-texture shape-drawer
           skin stage cursors textures viewport default-font default-font-params
           level-fn db world z-orders minimum-size world-unit-scale
           mouseover-eid paused? show-tile-grid? show-cell-entities? show-cell-occupied?
           show-body-bounds? show-potential-field-colors? play-sound! schemas]}]
  (reset! audio (into {}
                      (for [path sound-paths]
                        [path (.newSound Gdx/audio (.internal Gdx/files path))])))
  (reset! batch (SpriteBatch.))
  (reset! unit-scale 1)
  (reset! shape-drawer-texture (white-pixel-texture))
  (reset! shape-drawer (shape-drawer/create @batch
                                            (TextureRegion. ^Texture @shape-drawer-texture
                                                           (int 1) (int 0) (int 1) (int 1))))
  (reset! skin (create-skin! (.internal Gdx/files "skin/uiskin.json")))
  (let [s (Stage. (FitViewport. (float 1440) (float 900)) @batch)]
    (.setInputProcessor Gdx/input ^InputProcessor s)
    (reset! stage s))
  (set! (.initialTime ^TooltipManager (TooltipManager/getInstance)) 0)
  (Colors/put "PRETTY_NAME" (Color. 0.84 0.8 0.52 1))
  (reset! cursors (create-cursors! Gdx/files))
  (reset! textures (textures/create Gdx/files {:folder "resources/"
                                               :extensions #{"png" "bmp"}}))
  (reset! viewport (create-viewport! world-unit-scale))
  (reset! default-font (create-default-font! Gdx/files default-font-params))
  (let [{level-tiled-map :tiled-map
         level-start :start-position} (create-level! level-fn db @textures)]
    (reset! world (create-world level-tiled-map {:z-orders z-orders
                                                 :minimum-size minimum-size
                                                 :start-position level-start})))
  (doseq [^Actor actor (create-ui-actors world
                                         mouseover-eid
                                         paused?
                                         show-tile-grid?
                                         show-cell-entities?
                                         show-cell-occupied?
                                         show-body-bounds?
                                         show-potential-field-colors?
                                         unit-scale
                                         play-sound!
                                         @default-font
                                         @shape-drawer
                                         @skin
                                         @stage
                                         @textures
                                         @viewport)]
    (.addActor ^Stage @stage actor))
  (spawn-player! db schemas world @skin @stage @textures)
  (bind-player-eid! world)
  (spawn-map-creatures! db schemas world))

(defn- dispose!
  [{:keys [audio batch cursors default-font shape-drawer-texture skin textures world]}]
  (run! Sound/.dispose (vals @audio))
  (Disposable/.dispose @batch)
  (run! Disposable/.dispose (vals @cursors))
  (Disposable/.dispose @default-font)
  (Disposable/.dispose @shape-drawer-texture)
  (Disposable/.dispose @skin)
  (run! Disposable/.dispose (vals @textures))
  (Disposable/.dispose (:world/tiled-map @world)))

(defn- create-interaction-state [world mouseover-eid stage viewport]
  (let [player-eid (:world/player-eid @world)
        mouseover-eid @mouseover-eid
        mouseover-actor (current-mouseover-actor stage)
        world-mouse-position (world-mouse-position viewport)]
    (cond
      mouseover-actor
      [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor)]

      (and mouseover-eid
           (:entity/clickable @mouseover-eid))
      [:interaction-state/clickable-mouseover-eid
       {:clicked-eid mouseover-eid
        :in-click-range? (< (v2/distance (:entity/position @player-eid)
                                         (:entity/position @mouseover-eid))
                            (:entity/click-distance-tiles @player-eid))}]

      :else
      (if-let [skill-id (-> (.getRoot ^Stage stage)
                            (.findActor "moon.ui.action-bar")
                            action-bar/selected-skill)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              target-position (or (and mouseover-eid
                                       (:entity/position @mouseover-eid))
                                  world-mouse-position)
              effect-ctx {:effect/source player-eid
                          :effect/target mouseover-eid
                          :effect/target-position target-position
                          :effect/target-direction (v2/direction (:entity/position @player-eid)
                                                                target-position)}
              state (usable-state skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn- render!
  [{:keys [world mouseover-eid stage viewport render-z-order active-entities
           batch world-unit-scale default-font shape-drawer textures unit-scale
           show-body-bounds? show-tile-grid? show-cell-entities? show-cell-occupied?
           show-potential-field-colors? factions-iterations
           cursors play-sound! skin paused? pausing? state->pause-game?
           db schemas max-delta max-speed]
    :as ctx}]
  (ScreenUtils/clear 0 0 0 0)
  (update-mouseover-eid! world mouseover-eid @stage @viewport render-z-order)
  (update-active-entities! world active-entities)
  (set-camera-to-player! world @viewport)
  (.setProjectionMatrix ^Batch @batch (.combined (.getCamera ^Viewport @viewport)))
  (.begin ^Batch @batch)
  (tiled-map/draw! (:world/tiled-map @world)
                   @batch
                   world-unit-scale
                   (.getCamera ^Viewport @viewport)
                   (tile-color-setter*
                     {:ray-blocked? (partial raycaster/blocked? (:world/raycaster @world))
                      :explored-tile-corners (:world/explored-tile-corners @world)
                      :light-position (let [pos (.position (.getCamera ^Viewport @viewport))]
                                        [(.x pos) (.y pos) (.z pos)])
                      :see-all-tiles? false
                      :explored-tile-color (float-bits 0.5 0.5 0.5 1)
                      :visible-tile-color (float-bits 1 1 1 1)
                      :invisible-tile-color (float-bits 0 0 0 1)}))
  (.setColor ^Batch @batch (float 1) (float 1) (float 1) (float 1))
  (shape-drawer/with-line-width @shape-drawer world-unit-scale
    (reset! unit-scale world-unit-scale)
    (draw-tile-grid nil @shape-drawer @viewport show-tile-grid?)
    (draw-cell-debug nil @shape-drawer @viewport world
                     show-cell-entities? show-cell-occupied? show-potential-field-colors?
                     factions-iterations)
    (draw-entities! nil @shape-drawer @batch @default-font @textures unit-scale world-unit-scale
                    (let [[x y] (ui-mouse-position @stage)]
                      (mouseover-actor @stage x y))
                    (world-mouse-position @viewport)
                    (:world/raycaster @world) world show-body-bounds? active-entities render-z-order)
    (highlight-mouseover-tile nil @shape-drawer (world-mouse-position @viewport) world)
    (reset! unit-scale 1))
  (.end ^Batch @batch)
  (let [interaction-state (create-interaction-state world mouseover-eid @stage @viewport)
        cursor-key (cursor-key interaction-state world)
        cursors @cursors]
    (assert (contains? cursors cursor-key))
    (.setCursor Gdx/graphics ^Cursor (get cursors cursor-key))
    (handle-player-input! world interaction-state
                          play-sound! @skin @stage @textures @viewport
                          #(.isKeyPressed Gdx/input (int %))
                          #(.isButtonJustPressed Gdx/input (int %))))
  (reset! paused?
          (or #_error
              (and pausing?
                   (state->pause-game? (:state (:entity/fsm @(:world/player-eid @world))))
                   (not (or (.isKeyJustPressed Gdx/input (int (:unpause-once controls)))
                            (.isKeyPressed Gdx/input (int (:unpause-continously controls))))))))
  (tick-game! db schemas world active-entities paused?
              play-sound! @skin @stage @textures @viewport factions-iterations
              max-delta max-speed)
  (destroy-entities! db schemas world play-sound!)
  (doseq [[k f] key-pressed-actions]
    (when (.isKeyPressed Gdx/input (int k))
      (f ctx)))
  (doseq [[k f] key-just-pressed-actions]
    (when (.isKeyJustPressed Gdx/input (int k))
      (f ctx)))
  (.act ^Stage @stage)
  (.draw ^Stage @stage))

(defn- resize!
  [{:keys [stage viewport]} width height]
  (.update (.getViewport ^Stage @stage) width height true)
  (.update ^Viewport @viewport width height false))

(def listener
  (let [minimum-size 0.39
        max-delta 0.04
        z-orders [:z-order/on-ground
                  :z-order/ground
                  :z-order/flying
                  :z-order/effect]
        schemas (-> "schema.edn" io/resource slurp edn/read-string)
        audio (atom nil)
        ctx {:world-unit-scale (float (/ 48))
             :minimum-size minimum-size
             :max-delta max-delta
             :max-speed (/ minimum-size max-delta)
             :z-orders z-orders
             :render-z-order (apply hash-map (interleave z-orders (range)))
             :factions-iterations {:good 15
                                   :evil 5}
             :level-fn 
             create-vampire-level
             ;create-uf-caves
             ;create-modules-level
             :default-font-params {:path "fonts/films.EXL_____.ttf"
                                   :size 16
                                   :quality-scaling 2
                                   :use-integer-positions? false}
             :sound-paths (-> "sounds.edn" io/resource slurp edn/read-string)
             :audio audio
             :play-sound! (fn [sound-name]
                            (let [sounds @audio]
                              (assert (contains? sounds sound-name) (str sound-name))
                              (.play ^Sound (get sounds sound-name))))
             :batch (atom nil)
             :cursors (atom nil)
             :default-font (atom nil)
             :viewport (atom nil)
             :shape-drawer (atom nil)
             :shape-drawer-texture (atom nil)
             :textures (atom nil)
             :skin (atom nil)
             :stage (atom nil)
             :schemas schemas
             :db (create-db schemas (-> "properties.edn" io/resource slurp edn/read-string))
             :world (atom nil)
             :active-entities (atom nil)
             :mouseover-eid (atom nil)
             :paused? (atom false)
             :show-potential-field-colors? (atom nil)
             :show-cell-entities? (atom false)
             :show-cell-occupied? (atom false)
             :show-body-bounds? (atom false)
             :show-tile-grid? (atom false)
             :unit-scale (atom 1)
             :pausing? true
             :state->pause-game? {:active-skill false
                                  :stunned false
                                  :player-moving false
                                  :player-idle true
                                  :player-dead true
                                  :player-item-on-cursor true}}]
    (reify ApplicationListener
      (create [_]
        (create! ctx))
      (dispose [_]
        (dispose! ctx))
      (render [_]
        (render! ctx))
      (resize [_ width height]
        (resize! ctx width height))
      (pause [_])
      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. listener
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Moon")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
