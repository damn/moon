; TODO # 1
; TODO
; 1. step like item - validate incoming data - only then can I create info string if I know what data
; means what _schema_ is there !
; or every has 'info-depth' 'info' 'debug' 'player-info' ?
(ns moon.info-impl
  (:require [clojure.math :as math]
            [clojure.string :as str]
            [moon.info :as info]
            [moon.ops :as ops]
            [moon.order :as order]
            [moon.number :as number]
            [moon.stats :as stats]
            [moon.timer :as timer]))

(def ^:private non-val-max-stat-ks
  [:stats/movement-speed
   :stats/aggro-range
   :stats/reaction-time
   :stats/strength
   :stats/cast-speed
   :stats/attack-speed
   :stats/armor-save
   :stats/armor-pierce])

(defn- ops-info [ops modifier-k]
  (str/join "\n"
            (keep
             (fn [[k v]]
               (when-not (zero? v)
                 (str (case (math/signum v)
                        0.0 ""
                        1.0 "+"
                        -1.0 "")
                      (case k
                        :op/inc  (str v)
                        :op/mult (str v "%"))
                      " "
                      (str/capitalize (name modifier-k)))))
             (ops/sort ops))))

(comment
 (deftest info-texts
   (is (= (ops/info-text {:op/inc -4
                          :op/mult 24}
                         "Strength")
          "-4 Strength\n+24% Strength"))

   (is (= (ops/info-text {:op/inc -4
                          :op/mult 0}
                         "Strength")
          "-4 Strength"))

   (is (= (ops/info-text {:op/mult 35}
                         "Hitpoints")
          "+35% Hitpoints"))

   (is (= (ops/info-text {:op/inc -30
                          :op/mult 5}
                         "Hitpoints")
          "-30 Hitpoints\n+5% Hitpoints")))
 )

(defn stats-modifiers-info [mods]
  (when (seq mods) ; ?
    (str/join "\n" (keep (fn [[k ops]]
                           (ops-info ops k)) mods))))

(defn- remove-newlines [s]
  (let [new-s (-> s
                  (str/replace "\n\n" "\n")
                  (str/replace #"^\n" "")
                  str/trim-newline)]
    (if (= (count new-s) (count s))
      s
      (remove-newlines new-s))))

(def ^:private k->colors
  {:property/pretty-name "PRETTY_NAME"
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
   :entity/temp-modifier "LIGHT_GRAY"})

(def ^:private k-order
  [:property/pretty-name
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
   :entity-effects])

(def ^:private info-fns
  {:creature/level (fn [[_ v] _ctx]
                     (str "Level: " v))

   :entity/stats (fn [[_ stats] _ctx]
                   (str/join "\n" (concat
                                   ["*STATS*"
                                    (str "Mana: " (stats/get-mana stats))
                                    (str "Hitpoints: " (stats/get-hitpoints stats))]
                                   (for [stat-k non-val-max-stat-ks]
                                     (str (str/capitalize (name stat-k)) ": "
                                          (stats/get-stat-value stats stat-k))))))

   :effects.target/convert (fn [_ _ctx]
                             "Converts target to your side.")

   :effects.target/damage (fn [[_ {[min max] :damage/min-max}] _ctx]
                            (str min "-" max " damage")
                            #_(if source
                                (let [modified (stats/damage @source damage)]
                                  (if (= damage modified)
                                    (damage/info-text damage)
                                    (str (damage/info-text damage) "\nModified: " (damage/info modified))))
                                (damage/info-text damage)) ; property menu no source,modifiers
                            )

   :effects.target/kill (fn [_ _ctx]
                          "Kills target")

   :effects.target/melee-damage (fn [_ _ctx]
                                  (str "Damage based on entity strength."
                                       #_(when source
                                           (str "\n" (damage-info (entity->melee-damage @source))))))

   :effects.target/spiderweb (fn [_ _ctx]
                               "Spiderweb slows 50% for 5 seconds.")

   :effects.target/stun (fn [[_ duration] _ctx]
                          (str "Stuns for " (number/readable duration) " seconds"))

   :effects/spawn (fn [[_ {:keys [property/pretty-name]}] _ctx]
                    (str "Spawns a " pretty-name))

   :effects/target-all (fn [_ _ctx]
                         "All visible targets")

   :entity/delete-after-duration (fn [[_ counter] {:keys [ctx/elapsed-time]}]
                                   (str "Remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))

   :entity/faction (fn [[_ faction] _ctx]
                     (str "Faction: " (name faction)))

   :entity/fsm (fn [[_ fsm] _ctx]
                 (str "State: " (name (:state fsm))))

   :stats/modifiers (fn [[_ mods] _ctx]
                      (stats-modifiers-info mods))

   :entity/skills (fn [[_ skills] _ctx]
                    ; => recursive info-text leads to endless text wall
                    (when (seq skills)
                      (str "Skills: " (str/join "," (map name (keys skills))))))

   :entity/species (fn [[_ species] _ctx]
                     (str "Creature - " (str/capitalize (name species))))

   :entity/temp-modifier (fn [[_ {:keys [counter]}] {:keys [ctx/elapsed-time]}]
                           (str "Spiderweb - remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))

   :projectile/piercing? (fn [_ _ctx]
                           "Piercing")

   :property/pretty-name (fn [[_ v] _ctx]
                           v)

   :skill/cooling-down? (fn [[_ counter] {:keys [ctx/elapsed-time]}]
                          (str "Cooldown: " (number/readable (timer/ratio elapsed-time counter)) "/1"))

   :skill/action-time (fn [[_ v] _ctx]
                        (str "Action-Time: " (number/readable v) " seconds"))

   :skill/action-time-modifier-key (fn [[_ v] _ctx]
                                     (case v
                                       :stats/cast-speed "Spell"
                                       :stats/attack-speed "Attack"))

   :skill/cooldown (fn [[_ v] _ctx]
                     (str "Cooldown: " (number/readable v) " seconds"))

   :skill/cost (fn [[_ v] _ctx]
                 (str "Cost: " v " Mana"))

   :maxrange (fn [[_ v] _ctx]
               (str "Range: " v " Meters."))})

(comment
 (:skills/death-ray (:entity/skills @(:ctx/player-eid @moon.application/state)))
 ; cooling-down? is not set in the action-bar ....
 ; so not showing as ui not updated
 )

(defmethod info/text :info/entity [entity ctx]
  (let [component-info (fn [[k v]]
                         (let [s (if-let [info-fn (info-fns k)]
                                   (do
                                    (str k " - " (info-fn [k v] ctx))))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (order/sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable t
                             (str "*info-error* " k))) ; TODO this try/catch FIXME design error
                      (when (map? v)
                        (str "\n" (info/text v ctx))))))
         (str/join "\n")
         remove-newlines)))







(defn- valid-item? [item]
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

(defmethod info/text :info/item [item _ctx]
  (assert (valid-item? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (stats-modifiers-info (:stats/modifiers item)) "[]"))])))

(comment
 (let [item (get (:inventory.slot/shield (:entity/inventory @(:ctx/player-eid @moon.application/state)))
                 [0 0])]
   (item-info item)
   )

 {:entity/image #:image{:bounds [912 240 48 48], :file "images/items.png"},
  :stats/modifiers {},
  :item/slot :inventory.slot/shield,
  :property/id :items/shield-mystic-great,
  :property/pretty-name "Great Mystic Shield"}
 )















(defn- valid-skill? [skill]
  (= #{:property/id
       :property/pretty-name
       :entity/image
       :skill/action-time-modifier-key
       :skill/action-time
       :skill/start-action-sound
       :skill/effects
       :skill/cooldown
       :skill/cost}
     (set (keys skill))))

(comment
 (binding [*print-level* nil]
   (clojure.pprint/pprint (:skill/effects
                           (:skills/spawn (:entity/skills @(:ctx/player-eid @moon.application/state))))))
 )

(defn skill-info [skill]
  ; The core problem is that you’re eagerly unrolling your graph into nested maps (tree form). That guarantees infinite recursion if there are cycles.

  ; skill/effects is unrolled
  ; and then effects/spawn again a creature w. again skills unrolled ... ?
  ; and stats/e.g. not built
  ; unlike world entities ...
  ; what if a creature has spawn effect for its own type
  ; which again has spawn effect
  ; endless recursion ?
  ; ... relationships ... ? really unroll always?
  ; => buggy


  :effects/spawn ; -> full creature resolveed again with skills/effects/etc.
  ; why not every skill/action just 1 effect and the name etc inside the skill?

  )

(defn entity-info [entity ctx]
  ; dispatch entity type
  ; assert valid? projectile/creature/item/etc?

  ;  Now it gets interesting!
  ; We are not sure about the possible shape of our entities and which 'types' of shapes are there!!!
  ; => game data state space schema
  ; => game is a play with a state space
  ; e.g. change level
  ; spawn somethinbg
  ; ?
  )
