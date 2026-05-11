(ns moon.effect-impl
  (:require [clojure.math.vector2 :as v]
            [moon.effect :as effect]
            [moon.faction :as faction]
            [clojure.rand :refer [rand-int-between]]
            [moon.raycaster :as raycaster]
            [moon.stats :as stats]
            [moon.timer :as timer]))

; TODO use at projectile & also adjust rotation
(defn- start-point [body target-body]
  (v/add (:body/position body)
         (v/scale (v/direction (:body/position body)
                               (:body/position target-body))
                  (/ (:body/width body) 2))))

(defn- end-point [body target-body maxrange]
  (v/add (start-point body target-body)
         (v/scale (v/direction (:body/position body)
                               (:body/position target-body))
                  maxrange)))

(defn- in-range? [body target-body maxrange]
  (< (- (float (v/distance (:body/position body)
                           (:body/position target-body)))
        (float (/ (:body/width body)  2))
        (float (/ (:body/width target-body) 2)))
     (float maxrange)))

(defn- affected-targets [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(raycaster/line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(comment
 ; TODO applicable targets? e.g. projectiles/effect s/???item entiteis ??? check
 ; same code as in render entities on world view screens/world
 ; TODO showing one a bit further up
 ; maybe world view port is cut
 ; not quite showing correctly.
 (let [targets (creatures-in-los-of-player)]
   (count targets)
   #_(sort-by #(% 1) (map #(vector (:entity.creature/name @%)
                                   (:body/position (:entity/body @%))) targets)))

 )

(defn- create-double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]} ; wieso 0.98??
  (let [path-w (+ path-w 0.02) ;etwas gr�sser damit z.b. projektil nicht an ecken anst�sst
        v (v/direction [start-x start-y]
                       [target-y target-y])
        [normal1 normal2] (v/normal-vectors v)
        normal1 (v/scale normal1 (/ path-w 2))
        normal2 (v/scale normal2 (/ path-w 2))
        start1  (v/add [start-x  start-y]  normal1)
        start2  (v/add [start-x  start-y]  normal2)
        target1 (v/add [target-x target-y] normal1)
        target2 (v/add [target-x target-y] normal2)]
    [start1,target1,start2,target2]))

(defn- proj-start-point [body direction size]
  (v/add (:body/position body)
         (v/scale direction
                  (+ (/ (:body/width body) 2) size 0.1))))

(defn- entity->melee-damage [{:keys [entity/stats]}]
  (let [strength (or (stats/get-stat-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))

(defn- melee-damage-effect [entity]
  [:effects.target/damage (entity->melee-damage entity)])

; not in stats because projectile as source doesnt have stats
; FIXME I don't see it triggering with 10 armor save ... !
(defn- effective-armor-save [source-stats target-stats]
  (max (- (or (stats/get-stat-value source-stats :stats/armor-save)   0)
          (or (stats/get-stat-value target-stats :stats/armor-pierce) 0))
       0))

(comment

 (effective-armor-save {} {:stats/modifiers {:modifiers/armor-save {:op/inc 10}}
                           :stats/armor-save 0})
 ; broken
 (let [source* {:stats/armor-pierce 0.4}
       target* {:stats/armor-save   0.5}]
   (effective-armor-save source* target*))
 )

(defn calc-damage
  ([source target damage]
   (update (calc-damage source damage)
           :damage/min-max
           stats/apply-max
           (:stats/modifiers target)
           :modifier/damage-receive-max))
  ([source damage]
   (update damage
           :damage/min-max
           #(-> %
                (stats/apply-min (:stats/modifiers source) :modifier/damage-deal-min)
                (stats/apply-max (:stats/modifiers source) :modifier/damage-deal-max)))))

(defmethod effect/applicable? :effects.target/audiovisual
  [_ {:keys [effect/target]}]
  target)

(defmethod effect/useful? :effects.target/audiovisual
  [_ _effect-ctx _ctx]
  false)

(defmethod effect/handle :effects.target/audiovisual
  [[_ audiovisual] {:keys [effect/target]} _ctx]
  [[:tx/audiovisual (:body/position (:entity/body @target)) audiovisual]])

(defmethod effect/applicable? :effects.target/convert
  [_ {:keys [effect/source effect/target]}]
  (and target
       (= (:entity/faction @target)
          (faction/enemy (:entity/faction @source)))))

(defmethod effect/handle :effects.target/convert
  [_ {:keys [effect/source effect/target]} _ctx]
  [[:tx/assoc target :entity/faction (:entity/faction @source)]])

(defmethod effect/applicable? :effects.target/damage
  [_ {:keys [effect/target]}]
  (and target
       #_(:stats/hp @target))) ; not exist anymore ... bugfix .... -> is 'creature?'

(defmethod effect/handle :effects.target/damage
  [[_ damage] {:keys [effect/source effect/target]} _ctx]
  (let [source* @source
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
          (< (rand) (effective-armor-save (:entity/stats source*)
                                          (:entity/stats target*))))
     [[:tx/add-text-effect target "[WHITE]ARMOR" 0.3]]

     :else
     (let [min-max (:damage/min-max (calc-damage (:entity/stats source*)
                                                 (:entity/stats target*)
                                                 damage))
           dmg-amount (rand-int-between min-max)
           new-hp-val (max (- (hp 0) dmg-amount)
                           0)]
       [[:tx/assoc-in target [:entity/stats :stats/hp 0] new-hp-val]
        [:tx/event    target (if (zero? new-hp-val) :kill :alert)]
        [:tx/audiovisual (:body/position (:entity/body target*)) :audiovisuals/damage]
        [:tx/add-text-effect target (str "[RED]" dmg-amount "[]") 0.3]]))))

(defmethod effect/applicable? :effects.target/kill
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod effect/handle :effects.target/kill
  [_ {:keys [effect/target]} _ctx]
  [[:tx/event target :kill]])

(defmethod effect/applicable? :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx}]
  (effect/applicable? (melee-damage-effect @source) effect-ctx))

(defmethod effect/handle :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx} ctx]
  (effect/handle (melee-damage-effect @source) effect-ctx ctx))

(def ^:private spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def ^:private spiderweb-duration 5)

(defmethod effect/applicable? :effects.target/spiderweb
  [_ {:keys [effect/target]}]
  ; TODO has stats , for mod-add
  ; e,g, spiderweb on projectile leads to error
  (:entity/stats @target))

(defmethod effect/handle :effects.target/spiderweb
  [_ {:keys [effect/target]} {:keys [ctx/elapsed-time]}]
  ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
  (when-not (:entity/temp-modifier @target)
    [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                              :counter (timer/create elapsed-time spiderweb-duration)}]
     [:tx/update target :entity/stats stats/add spiderweb-modifiers]]))

(defmethod effect/applicable? :effects.target/stun
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod effect/handle :effects.target/stun
  [[_ duration] {:keys [effect/target]} _ctx]
  [[:tx/event target :stun duration]])

(defmethod effect/applicable? :effects/audiovisual
  [_ {:keys [effect/target-position]}]
  target-position)

(defmethod effect/useful? :effects/audiovisual
  [_ _effect-ctx _ctx]
  false)

(defmethod effect/handle :effects/audiovisual
  [[_ audiovisual] {:keys [effect/target-position]} _ctx]
  [[:tx/audiovisual target-position audiovisual]])

(defmethod effect/applicable? :effects/projectile
  [_ {:keys [effect/target-direction]}]
  ; TODO for npcs need target -- anyway only with direction
  ; faction @ source also ?
  target-direction)

(defmethod effect/useful? :effects/projectile
  [[_ {:keys [projectile/max-range] :as projectile}]
   {:keys [effect/source effect/target]}
   {:keys [ctx/raycaster]}]
  ; TODO valid params direction has to be  non-nil (entities not los player ) ?
  (let [source-p (:body/position (:entity/body @source))
        target-p (:body/position (:entity/body @target))]
    ; is path blocked ereally needed? we need LOS also right to have a target-direction as AI?
    (and (not (let [[start1,target1,start2,target2] (create-double-ray-endpositions source-p target-p (:projectile/size projectile))]
                (or
                 (raycaster/blocked? raycaster start1 target1)
                 (raycaster/blocked? raycaster start2 target2))))
         ; TODO not taking into account body sizes
         (< (v/distance source-p ; entity/distance function protocol EntityPosition
                        target-p)
            max-range))))

(defmethod effect/handle :effects/projectile
  [[_ projectile] {:keys [effect/source effect/target-direction]} _ctx]
  [[:tx/spawn-projectile
    {:position (proj-start-point (:entity/body @source)
                                 target-direction
                                 (:projectile/size projectile))
     :direction target-direction
     :faction (:entity/faction @source)}
    projectile]])

(defmethod effect/applicable? :effects/spawn
  [_ {:keys [effect/source effect/target-position]}]
  (and (:entity/faction @source)
       target-position))

(defmethod effect/handle :effects/spawn
  [[_ {:keys [property/id] :as property}]
   {:keys [effect/source effect/target-position]}
   _ctx]
  [[:tx/spawn-creature {:position target-position
                        :creature-property property
                        :components {:entity/fsm {:fsm :fsms/npc
                                                  :initial-state :npc-idle}
                                     :entity/faction (:entity/faction @source)}}]])

(defmethod effect/applicable? :effects/target-all
  [_ _] ; TODO check ..
  true)

(defmethod effect/useful? :effects/target-all
  [_ _effect-ctx _ctx]
  false)

(defmethod effect/handle :effects/target-all
  [[_ {:keys [entity-effects]}]
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/colors
           ctx/raycaster]}]
  (let [source* @source]
    (apply concat
           (for [target (affected-targets active-entities raycaster source*)]
             [[:tx/spawn-line
               {:start (:body/position (:entity/body source*)) #_(start-point source* target*)
                :end (:body/position (:entity/body @target))
                :duration 0.05
                :color (:colors/target-all-line colors)
                :thick? true}]
              [:tx/effect
               {:effect/source source
                :effect/target target}
               entity-effects]]))))

(defmethod effect/render :effects/target-all
  [_
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/colors
           ctx/raycaster]}]
  (let [source* @source]
    (for [target* (map deref (affected-targets active-entities raycaster source*))]
      [:draw/line
       (:body/position (:entity/body source*)) #_(start-point source* target*)
       (:body/position (:entity/body target*))

       (:colors/target-all-render colors)])))

(defmethod effect/applicable? :effects/target-entity
  [[_ {:keys [entity-effects]}] {:keys [effect/target] :as effect-ctx}]
  (and target
       (seq (filter #(effect/applicable? % effect-ctx) entity-effects))))

(defmethod effect/useful? :effects/target-entity
  [[_ {:keys [maxrange]}] {:keys [effect/source effect/target]} _ctx]
  (in-range? (:entity/body @source)
             (:entity/body @target)
             maxrange))

(defmethod effect/handle :effects/target-entity
  [[_ {:keys [maxrange entity-effects]}]
   {:keys [effect/source effect/target] :as effect-ctx}
   {:keys [ctx/colors]}]
  (let [body        (:entity/body @source)
        target-body (:entity/body @target)]
    (if (in-range? body target-body maxrange)
      [[:tx/spawn-line {:start (start-point body target-body)
                        :end (:body/position target-body)
                        :duration 0.05
                        :color (:colors/target-entity-line colors)
                        :thick? true}]
       [:tx/effect effect-ctx entity-effects]]
      [[:tx/audiovisual
        (end-point body target-body maxrange)
        :audiovisuals/hit-ground]])))

(defmethod effect/render :effects/target-entity
  [[_ {:keys [maxrange]}]
   {:keys [effect/source effect/target]}
   {:keys [ctx/colors]}]
  (when target
    (let [body        (:entity/body @source)
          target-body (:entity/body @target)]
      [[:draw/line
        (start-point body target-body)
        (end-point body target-body maxrange)
        (if (in-range? body target-body maxrange)
          (:colors/target-entity-in-range colors)
          (:colors/target-entity-not-in-range colors))]])))
