(ns moon.entity.state.active-skill
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clojure.math :as math]
            [moon.effect :as effect]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.timer :as timer]
            [moon.raycaster :as raycaster]))

(defn- apply-action-speed-modifier [{:keys [entity/stats]} skill action-time]
  (/ action-time
     (or (stats/get-stat-value stats (:skill/action-time-modifier-key skill))
         1)))

(defn create
  [[_k [skill effect-ctx]] eid {:keys [ctx/elapsed-time]}]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (apply-action-speed-modifier @eid skill)
                 (timer/create elapsed-time))})

(defn- update-effect-ctx
  [raycaster {:keys [effect/source effect/target] :as effect-ctx}]
  (if (and target
           (not (:entity/destroyed? @target))
           (raycaster/line-of-sight? raycaster @source @target))
    effect-ctx
    (dissoc effect-ctx :effect/target)))

(defn tick
  [[_k {:keys [skill effect-ctx counter]}]
   eid
   {:keys [ctx/elapsed-time
           ctx/raycaster]}]
  (let [effect-ctx (update-effect-ctx raycaster effect-ctx)]
    (cond
     (not (seq (filter #(effect/applicable? % effect-ctx)
                       (:skill/effects skill))))
     [[:tx/event eid :action-done]]

     (timer/stopped? elapsed-time counter)
     [[:tx/effect effect-ctx (:skill/effects skill)]
      [:tx/event eid :action-done]])))

(def ^:private skill-image-radius-world-units
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- draw-skill-image
  [texture-region entity [x y] action-counter-ratio]
  (let [radius skill-image-radius-world-units
        y (+ (float y)
             (float (/ (:body/height (:entity/body entity)) 2))
             (float 0.15))
        center [x (+ y radius)]]
    [[:draw/filled-circle center radius (color/float-bits [1 1 1 0.125])]
     [:draw/sector
      center
      radius
      (math/to-radians 90) ; start-angle
      (math/to-radians (* (float action-counter-ratio) 360)) ; degree
      (color/float-bits [1 1 1 0.5])]
     [:draw/texture-region texture-region [(- (float x) radius) y]]]))

(defn render
  [[_k {:keys [skill effect-ctx counter]}]
   entity
   {:keys [ctx/elapsed-time
           ctx/textures]
    :as ctx}]
  (let [{:keys [entity/image skill/effects]} skill]
    (concat (draw-skill-image (textures/texture-region textures image)
                              entity
                              (:body/position (:entity/body entity))
                              (timer/ratio elapsed-time counter))
            (mapcat #(effect/render % effect-ctx ctx)  ; update-effect-ctx here too ?
                    effects))))

(defn enter
  [[_k {:keys [skill]}] eid]
  [[:tx/sound (:skill/start-action-sound skill)]
   [:tx/set-cooldown eid skill]
   [:tx/update eid :entity/stats stats/pay-mana-cost (:skill/cost skill)]])

(defn cursor
  [_ _eid _ctx]
  :cursors/sandclock)

(defn pause-game?
  [_]
  false)
