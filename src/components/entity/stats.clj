(ns components.entity.stats
  (:require [clojure.string :as str]
            [malli.core :as m]
            [gdx.graphics.color :as color]
            [utils.core :as utils :refer [k->pretty-name readable-number]]
            [utils.random :as random]
            [core.val-max :refer [val-max-schema val-max-ratio lower-than-max? set-to-max]]
            [core.component :as component :refer [defcomponent]]
            [core.data :as data]
            [core.effect :as effect]
            [core.entity :as entity]
            [core.graphics :as g]
            [core.modifiers :as modifiers]
            [core.operation :as op]))

(defn- conj-value [value]
  (fn [values]
    (conj values value)))

(defn- remove-value [value]
  (fn [values]
    {:post [(= (count %) (dec (count values)))]}
    (utils/remove-one values value)))

(defn- txs-update-modifiers [entity modifiers f]
  (for [[modifier-k operations] modifiers
        [operation-k value] operations]
    [:tx.entity/update-in entity [:entity/stats :stats/modifiers modifier-k operation-k] (f value)]))

(comment
 (= (txs-update-modifiers :entity
                         {:modifier/hp {:op/max-inc 5
                                        :op/max-mult 0.3}
                          :modifier/movement-speed {:op/mult 0.1}}
                         (fn [value] :fn))
    [[:tx.entity/update-in :entity [:entity/stats :stats/modifiers :modifier/hp :op/max-inc] :fn]
     [:tx.entity/update-in :entity [:entity/stats :stats/modifiers :modifier/hp :op/max-mult] :fn]
     [:tx.entity/update-in :entity [:entity/stats :stats/modifiers :modifier/movement-speed :op/mult] :fn]])
 )

(defcomponent :tx/apply-modifiers {}
  (effect/do! [[_ entity modifiers] _ctx]
    (txs-update-modifiers entity modifiers conj-value)))

(defcomponent :tx/reverse-modifiers {}
  (effect/do! [[_ entity modifiers] _ctx]
    (txs-update-modifiers entity modifiers remove-value)))

; DRY ->effective-value (summing)
; also: sort-by op/order @ modifier/info-text itself (so player will see applied order)
(defn- sum-operation-values [stats-modifiers]
  (for [[modifier-k operations] stats-modifiers
        :let [operations (for [[operation-k values] operations
                               :let [value (apply + values)]
                               :when (not (zero? value))]
                           [operation-k value])]
        :when (seq operations)]
    [modifier-k operations]))

(defn- stats-modifiers-info-text [stats-modifiers]
  (let [modifiers (sum-operation-values stats-modifiers)]
    (when (seq modifiers)
      (modifiers/info-text modifiers))))

(defn- ->effective-value [base-value modifier-k stats]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (->> stats
       :stats/modifiers
       modifier-k
       (sort-by op/order)
       (reduce (fn [base-value [operation-k values]]
                 (op/apply [operation-k (apply + values)] base-value))
               base-value)))

(comment
 (and
  (= (->effective-value [5 10]
                        :modifier/damage-deal
                        {:stats/modifiers {:modifier/damage-deal {:op/val-inc [30]
                                                                  :op/val-mult [0.5]}}})
     [52 52])
  (= (->effective-value [5 10]
                        :modifier/damage-deal
                        {:stats/modifiers {:modifier/damage-deal {:op/val-inc [30]}
                                           :stats/fooz-barz {:op/babu [1 2 3]}}})
     [35 35])
  (= (->effective-value [5 10]
                        :modifier/damage-deal
                        {:stats/modifiers {}})
     [5 10])
  (= (->effective-value [100 100]
                        :modifier/hp
                        {:stats/modifiers {:modifier/hp {:op/max-inc [10 1]
                                                         :op/max-mult [0.5]}}})
     [100 166])
  (= (->effective-value 3
                        :modifier/movement-speed
                        {:stats/modifiers {:modifier/movement-speed {:op/inc [2]
                                                                     :op/mult [0.1 0.2]}}})
     6.5))
 )

(defn- stat-k->modifier-k [stat-k]
  (keyword "modifier" (name stat-k)))

(defn- stat-k->effective-value [stat-k stats]
  (when-let [base-value (stat-k stats)]
    (->effective-value base-value (stat-k->modifier-k stat-k) stats)))

(def ^:private stats-info-text-order
  [:stats/hp
   :stats/mana
   :stats/movement-speed
   :stats/strength
   :stats/cast-speed
   :stats/attack-speed
   :stats/armor-save
   :stats/armor-pierce
   ])

; widgets / icons ? (see WoW )
; * HP color based on ratio like hp bar samey (take same color definitions etc.)
; * mana color same in the whole app
; * red positive/green negative
; * readable-number on ->effective-value but doesn't work on val-max ->pretty-value fn ?
(defn- stats-info-texts [stats]
  (str/join "\n"
            (for [stat-k stats-info-text-order
                  :let [value (stat-k->effective-value stat-k stats)]
                  :when value]
              (str (k->pretty-name stat-k) ": " value))))

(defn defmodifier [modifier-k operations]
  (defcomponent modifier-k (data/components operations)))

(defn defstat [stat-k attr-m & {:keys [operations]}]
  (defcomponent stat-k attr-m)
  (when (seq operations)
    (defmodifier (stat-k->modifier-k stat-k) operations)))

(defstat :stats/hp   data/pos-int-attr :operations [:op/max-inc :op/max-mult])
(defstat :stats/mana data/nat-int-attr :operations [:op/max-inc :op/max-mult])

(defn- effect-k->stat-k [effect-k]
  (keyword "stats" (name effect-k)))

; TODO says here 'Minimum' hp instead of just 'HP'
; Sets to 0 but don't kills
; Could even set to a specific value ->
; op/set-to-ratio 0.5 ....
; sets the hp to 50%...

; is called ::stat-effect so it doesn't show up in (data/namespace-components :effect) list in editor
; for :skill/effects
(defcomponent ::stat-effect {}
  (effect/text [[k operations] _effect-ctx]
    (str/join "\n"
              (for [operation operations]
                (str (op/info-text operation) " " (k->pretty-name k)))))

  (effect/applicable? [[k _] {:keys [effect/target]}]
    (and target
         (entity/stat @target (effect-k->stat-k k))))

  (effect/useful? [_ _effect-ctx] true)

  (effect/do! [[effect-k operations] {:keys [effect/target]}]
    (let [stat-k (effect-k->stat-k effect-k)]
      (when-let [effective-value (entity/stat @target stat-k)]
        [[:tx.entity/assoc-in target [:entity/stats stat-k]
          (reduce (fn [value operation] (op/apply operation value))
                  effective-value
                  operations)]]))))

(defcomponent :effect/hp (data/components [:op/val-inc :op/val-mult :op/max-inc :op/max-mult]))
(derive :effect/hp ::stat-effect)

(defcomponent :effect/mana (data/components [:op/val-inc :op/val-mult :op/max-inc :op/max-mult]))
(derive :effect/mana ::stat-effect)

; * TODO clamp/post-process effective-values @ stat-k->effective-value
; * just don't create movement-speed increases too much?
; * dont remove strength <0 or floating point modifiers  (op/int-inc ?)
; * cast/attack speed dont decrease below 0 ??

; TODO clamp between 0 and max-speed ( same as movement-speed-schema )
(defstat :stats/movement-speed {:widget :text-field
                                :schema (m/form entity/movement-speed-schema)}
  :operations [:op/inc :op/mult])

; TODO show the stat in different color red/green if it was permanently modified ?
; or an icon even on the creature
; also we want audiovisuals always ...
(defcomponent :effect/movement-speed (data/components [:op/mult]))
(derive :effect/movement-speed ::stat-effect)

; TODO clamp into ->pos-int
(defstat :stats/strength data/nat-int-attr :operations [:op/inc])

; TODO here >0
(let [doc "action-time divided by this stat when a skill is being used.
          Default value 1.

          For example:
          attack/cast-speed 1.5 => (/ action-time 1.5) => 150% attackspeed."
      skill-speed-stat (assoc data/pos-attr :doc doc)
      operations [:op/inc]]
  (defstat :stats/cast-speed   skill-speed-stat :operations operations)
  (defstat :stats/attack-speed skill-speed-stat :operations operations))

; TODO bounds
(defstat :stats/armor-save   {:widget :text-field :schema number?} :operations [:op/inc])
(defstat :stats/armor-pierce {:widget :text-field :schema number?} :operations [:op/inc])

; TODO needs to be there for each npc - make non-removable (added to all creatures)
(defstat :stats/aggro-range (assoc data/nat-int-attr :optional? false))

; TODO kommt aufs gleiche raus if we have +1 min damage or +1 max damage?
; just inc/mult ?
; or even mana/hp does it make a difference ?
(defmodifier :modifier/damage-deal    [:op/val-inc :op/val-mult :op/max-inc :op/max-mult])
(defmodifier :modifier/damage-receive [:op/inc :op/mult])

(defcomponent :stats/modifiers (data/components [:modifier/damage-deal
                                                 :modifier/damage-receive]))

(extend-type core.entity.Entity
  entity/Stats
  (stat [{:keys [entity/stats]} stat-k]
    (stat-k->effective-value stat-k stats)))

; TODO remove vector shaboink -> gdx.graphics.color/->color use.
(def ^:private hpbar-colors
  {:green     [0 0.8 0]
   :darkgreen [0 0.5 0]
   :yellow    [0.5 0.5 0]
   :red       [0.5 0 0]})

(defn- hpbar-color [ratio]
  (let [ratio (float ratio)
        color (cond
                (> ratio 0.75) :green
                (> ratio 0.5)  :darkgreen
                (> ratio 0.25) :yellow
                :else          :red)]
    (color hpbar-colors)))

(def ^:private borders-px 1)

(defn- build-modifiers [modifiers]
  (into {} (for [[modifier-k operations] modifiers]
             [modifier-k (into {} (for [[operation-k value] operations]
                                    [operation-k [value]]))])))

(comment
 (= {:modifier/damage-receive {:op/mult [-0.9]}}
    (build-modifiers {:modifier/damage-receive {:op/mult -0.9}}))
 )

(defcomponent :entity/stats (data/components-attribute :stats)
  stats
  (component/create [_ _ctx]
    (-> stats
        (update :stats/hp (fn [hp] (when hp [hp hp])))
        (update :stats/mana (fn [mana] (when mana [mana mana])))
        (update :stats/modifiers build-modifiers)))

  (component/info-text [_ _ctx]
    (str (stats-info-texts stats)
         "\n"
         (stats-modifiers-info-text (:stats/modifiers stats))))

  (entity/render-info [_
                       {:keys [width half-width half-height entity/mouseover?] :as entity*}
                       g
                       _ctx]
    (when-let [hp (entity/stat entity* :stats/hp)]
      (let [ratio (val-max-ratio hp)
            [x y] (:position entity*)]
        (when (or (< ratio 1) mouseover?)
          (let [x (- x half-width)
                y (+ y half-height)
                height (g/pixels->world-units g entity/hpbar-height-px)
                border (g/pixels->world-units g borders-px)]
            (g/draw-filled-rectangle g x y width height color/black)
            (g/draw-filled-rectangle g
                                     (+ x border)
                                     (+ y border)
                                     (- (* width ratio) (* 2 border))
                                     (- height (* 2 border))
                                     (hpbar-color ratio))))))))

(defcomponent :tx.entity.stats/pay-mana-cost {}
  (effect/do! [[_ entity cost] _ctx]
    (let [mana-val ((entity/stat @entity :stats/mana) 0)]
      (assert (<= cost mana-val))
      [[:tx.entity/assoc-in entity [:entity/stats :stats/mana 0] (- mana-val cost)]])))

(comment
 (let [mana-val 4
       entity (atom (entity/map->Entity {:entity/stats {:stats/mana [mana-val 10]}}))
       mana-cost 3
       resulting-mana (- mana-val mana-cost)]
   (= (effect/do! [:tx.entity.stats/pay-mana-cost entity mana-cost] nil)
      [[:tx.entity/assoc-in entity [:entity/stats :stats/mana 0] resulting-mana]]))
 )

; breaks with defcomponent because the sys-impls are not a list but a 'cons'
#_(defcomponent ::effect/stats-mana-set-to-max {:widget :label
                                              :schema [:= true]
                                              :default-value true}
  (effect/text [_ _effect-ctx]
    (str "Sets " (name stat) " to max."))

  (effect/applicable? ~'[_ _effect-ctx] true)

  (effect/useful? ~'[_ {:keys [effect/source]}]
    (lower-than-max? (~stat (:entity/stats @~'source))))

  (effect/do! ~'[_ {:keys [effect/source]}]
    [[:tx/sound "sounds/bfxr_click.wav"]
     [:tx.entity/assoc-in ~'source [:entity/stats ~stat] (set-to-max (~stat (:entity/stats @~'source)))]]))

; TODO sound will be played twice !
; => or re-add effect/sound & make it useful? false ....
; also this is target-self
#_(def-set-to-max-effect :stats/hp)
#_(def-set-to-max-effect :stats/mana)

(defn- entity*->melee-damage [entity*]
  (let [strength (or (entity/stat entity* :stats/strength) 0)]
    {:damage/min-max [strength strength]}))

(defn- damage-effect [{:keys [effect/source]}]
  [:effect/damage (entity*->melee-damage @source)])

(defcomponent :effect/melee-damage {}
  (effect/text [_ {:keys [effect/source] :as effect-ctx}]
    (str "Damage based on entity strength."
         (when source
           (str "\n" (effect/text (damage-effect effect-ctx)
                                  effect-ctx)))))

  (effect/applicable? [_ effect-ctx]
    (effect/applicable? (damage-effect effect-ctx) effect-ctx))

  (effect/do! [_ ctx]
    [(damage-effect ctx)]))

(defn- effective-armor-save [source* target*]
  (max (- (or (entity/stat target* :stats/armor-save) 0)
          (or (entity/stat source* :stats/armor-pierce) 0))
       0))

(comment
 ; broken
 (let [source* {:entity/stats {:stats/armor-pierce 0.4}}
       target* {:entity/stats {:stats/armor-save   0.5}}]
   (effective-armor-save source* target*))
 )

(defn- armor-saves? [source* target*]
  (< (rand) (effective-armor-save source* target*)))

(defn- ->effective-damage [damage source*]
  (update damage :damage/min-max ->effective-value :modifier/damage-deal (:entity/stats source*)))

(comment
 (let [->stats (fn [mods] {:entity/stats {:stats/modifiers mods}})]
   (and
    (= (->effective-damage {:damage/min-max [5 10]}
                           (->stats {:modifier/damage-deal {:op/val-inc [1 5 10]
                                                            :op/val-mult [0.2 0.3]
                                                            :op/max-mult [1]}}))
       #:damage{:min-max [31 62]})

    (= (->effective-damage {:damage/min-max [5 10]}
                           (->stats {:modifier/damage-deal {:op/val-inc [1]}}))
       #:damage{:min-max [6 10]})

    (= (->effective-damage {:damage/min-max [5 10]}
                           (->stats {:modifier/damage-deal {:op/max-mult [2]}}))
       #:damage{:min-max [5 30]})

    (= (->effective-damage {:damage/min-max [5 10]}
                           (->stats nil))
       #:damage{:min-max [5 10]}))))

(defn- damage->text [{[min-dmg max-dmg] :damage/min-max}]
  (str min-dmg "-" max-dmg " damage"))

(defcomponent :damage/min-max data/val-max-attr)

(defcomponent :effect/damage (data/map-attribute :damage/min-max)
  damage
  (effect/text [_ {:keys [effect/source]}]
    (if source
      (let [modified (->effective-damage damage @source)]
        (if (= damage modified)
          (damage->text damage)
          (str (damage->text damage) "\nModified: " (damage->text modified))))
      (damage->text damage))) ; property menu no source,modifiers

  (effect/applicable? [_ {:keys [effect/target]}]
    (and target
         (entity/stat @target :stats/hp)))

  (effect/do! [_ {:keys [effect/source effect/target]}]
    (let [source* @source
          target* @target
          hp (entity/stat target* :stats/hp)]
      (cond
       (zero? (hp 0))
       []

       (armor-saves? source* target*)
       [[:tx/add-text-effect target "[WHITE]ARMOR"]] ; TODO !_!_!_!_!_!

       :else
       (let [{:keys [damage/min-max]} (->effective-damage damage source*)
             ;_ (println "\nmin-max:" min-max)
             dmg-amount (random/rand-int-between min-max)
             ;_ (println "dmg-amount: " dmg-amount)
             dmg-amount (->effective-value dmg-amount :modifier/damage-receive (:entity/stats target*))
             ;_ (println "effective dmg-amount: " dmg-amount)
             new-hp-val (max (- (hp 0) dmg-amount) 0)]
         [[:tx.entity/audiovisual (:position target*) :audiovisuals/damage]
          [:tx/add-text-effect target (str "[RED]" dmg-amount)]
          [:tx.entity/assoc-in target [:entity/stats :stats/hp 0] new-hp-val]
          [:tx/event target (if (zero? new-hp-val) :kill :alert)]])))))