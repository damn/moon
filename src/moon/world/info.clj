(ns moon.world.info
  (:require [clojure.string :as str]
            [clojure.math :as math]
            [moon.entity.stats.info]
            [moon.ops :as ops]
            [moon.timer :as timer]
            [moon.utils :as utils]))

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

(defmulti text (fn [object world]
                 (cond (:item/slot object)
                       :info/item
                       ;(:skill/action-time object)
                       ;:info/skill
                       :else
                       :info/entity)))

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
  {:creature/level (fn [[_ v] _world]
                     (str "Level: " v))

   :entity/stats moon.entity.stats.info/text

   :effects.target/convert (fn [_ _world]
                             "Converts target to your side.")

   :effects.target/damage (fn [[_ {[min max] :damage/min-max}] _world]
                            (str min "-" max " damage")
                            #_(if source
                                (let [modified (stats/damage @source damage)]
                                  (if (= damage modified)
                                    (damage/info-text damage)
                                    (str (damage/info-text damage) "\nModified: " (damage/info modified))))
                                (damage/info-text damage)) ; property menu no source,modifiers
                            )

   :effects.target/kill (fn [_ _world]
                          "Kills target")

   :effects.target/melee-damage (fn [_ _world]
                                  (str "Damage based on entity strength."
                                       #_(when source
                                           (str "\n" (damage-info (entity->melee-damage @source))))))

   :effects.target/spiderweb (fn [_ _world]
                               "Spiderweb slows 50% for 5 seconds.")

   :effects.target/stun (fn [[_ duration] _world]
                          (str "Stuns for " (utils/readable-number duration) " seconds"))

   :effects/spawn (fn [[_ {:keys [property/pretty-name]}] _world]
                    (str "Spawns a " pretty-name))

   :effects/target-all (fn [_ _world]
                         "All visible targets")

   :entity/delete-after-duration (fn [[_ counter] {:keys [world/elapsed-time]}]
                                   (str "Remaining: " (utils/readable-number (timer/ratio elapsed-time counter)) "/1"))

   :entity/faction (fn [[_ faction] _world]
                     (str "Faction: " (name faction)))

   :entity/fsm (fn [[_ fsm] _world]
                 (str "State: " (name (:state fsm))))

   :stats/modifiers (fn [[_ mods] _world]
                      (stats-modifiers-info mods))

   :entity/skills (fn [[_ skills] _world]
                    ; => recursive info-text leads to endless text wall
                    (when (seq skills)
                      (str "Skills: " (str/join "," (map name (keys skills))))))

   :entity/species (fn [[_ species] _world]
                     (str "Creature - " (str/capitalize (name species))))

   :entity/temp-modifier (fn [[_ {:keys [counter]}] {:keys [world/elapsed-time]}]
                           (str "Spiderweb - remaining: " (utils/readable-number (timer/ratio elapsed-time counter)) "/1"))

   :projectile/piercing? (fn [_ _world]
                           "Piercing")

   :property/pretty-name (fn [[_ v] _world]
                           v)

   :skill/cooling-down? (fn [[_ counter] {:keys [world/elapsed-time]}]
                          (str "Cooldown: " (utils/readable-number (timer/ratio elapsed-time counter)) "/1"))

   :skill/action-time (fn [[_ v] _world]
                        (str "Action-Time: " (utils/readable-number v) " seconds"))

   :skill/action-time-modifier-key (fn [[_ v] _world]
                                     (case v
                                       :stats/cast-speed "Spell"
                                       :stats/attack-speed "Attack"))

   :skill/cooldown (fn [[_ v] _world]
                     (str "Cooldown: " (utils/readable-number v) " seconds"))

   :skill/cost (fn [[_ v] _world]
                 (str "Cost: " v " Mana"))

   :maxrange (fn [[_ v] _world]
               (str "Range: " v " Meters."))})

(comment
 (:skills/death-ray (:entity/skills @(:world/player-eid (:ctx/world @moon.application/state))))
 ; cooling-down? is not set in the action-bar ....
 ; so not showing as ui not updated
 )

(defmethod text :info/entity [entity world]
  (let [component-info (fn [[k v]]
                         (let [s (if-let [info-fn (info-fns k)]
                                   (do
                                    (str k " - " (info-fn [k v] world))))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (utils/sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable t
                             (str "*info-error* " k)))
                      (when (map? v)
                        (str "\n" (text v world))))))
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

(defmethod text :info/item [item _world]
  (assert (valid-item? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (stats-modifiers-info (:stats/modifiers item)) "[]"))])))

(comment
 (let [item (get (:inventory.slot/shield (:entity/inventory @(:world/player-eid (:ctx/world @moon.application/state))))
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
                           (:skills/spawn (:entity/skills @(:world/player-eid (:ctx/world @moon.application/state)))))))
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

(defn entity-info [entity world]
  ; dispatch entity type
  ; assert valid? projectile/creature/item/etc?

  ;  Now it gets interesting!
  ; We are not sure about the possible shape of our entities and which 'types' of shapes are there!!!
  ; => game data state space schema
  )
