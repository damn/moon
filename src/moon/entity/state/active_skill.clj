(ns moon.entity.state.active-skill
  (:require [moon.effect :as effect]
            [moon.entity :as entity]
            [moon.entity.state :as state]
            [moon.entity.stats :as stats]
            [moon.textures :as textures]
            [moon.timer :as timer]
            [moon.world :as world]))

(defn- apply-action-speed-modifier [{:keys [entity/stats]} skill action-time]
  (/ action-time
     (or (stats/get-stat-value stats (:skill/action-time-modifier-key skill))
         1)))

(defmethod state/create :active-skill
  [[_k [skill effect-ctx]] eid {:keys [ctx/world]}]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (apply-action-speed-modifier @eid skill)
                 (timer/create (:world/elapsed-time world)))})

(defn- update-effect-ctx
  [world {:keys [effect/source effect/target] :as effect-ctx}]
  (if (and target
           (not (:entity/destroyed? @target))
           (world/line-of-sight? world @source @target))
    effect-ctx
    (dissoc effect-ctx :effect/target)))

(defmethod entity/tick :active-skill
  [[_k {:keys [skill effect-ctx counter]}]
   eid
   {:keys [ctx/world]}]
  (let [{:keys [world/elapsed-time]} world
        effect-ctx (update-effect-ctx world effect-ctx)]
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
    [[:draw/filled-circle center radius [1 1 1 0.125]]
     [:draw/sector
      center
      radius
      90 ; start-angle
      (* (float action-counter-ratio) 360) ; degree
      [1 1 1 0.5]]
     [:draw/texture-region texture-region [(- (float x) radius) y]]]))

(defmethod entity/render :active-skill
  [[_k {:keys [skill effect-ctx counter]}]
   entity
   {:keys [ctx/textures
           ctx/world]
    :as ctx}]
  (let [{:keys [entity/image skill/effects]} skill]
    (concat (draw-skill-image (textures/texture-region textures image)
                              entity
                              (:body/position (:entity/body entity))
                              (timer/ratio (:world/elapsed-time world) counter))
            (mapcat #(effect/render % effect-ctx ctx)  ; update-effect-ctx here too ?
                    effects))))

(defmethod state/enter :active-skill
  [[_k {:keys [skill]}] eid]
  [[:tx/sound (:skill/start-action-sound skill)]
   [:tx/set-cooldown eid skill]
   [:tx/update eid :entity/stats stats/pay-mana-cost (:skill/cost skill)]])

(defmethod state/cursor :active-skill
  [_ _eid _ctx]
  :cursors/sandclock)

(defmethod state/pause-game? :active-skill
  [_]
  false)
