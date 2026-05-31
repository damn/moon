(ns game.info
  (:require [clojure.core-ext :refer [sort-by-k-order]]
            [clojure.string :as str]
            [game.constants :refer [k->colors k-order]]
            [game.info-fns :as info-fns]))

(defmulti text (fn [object ctx]
                 (cond (:item/slot object)
                       :info/item
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

(comment
 (:skills/death-ray (:entity/skills @(:ctx/player-eid @dev.application/state)))
 ; cooling-down? is not set in the action-bar ....
 ; so not showing as ui not updated
 )

(defmethod text :info/entity [entity ctx]
  (let [component-info (fn [[k v]]
                         (let [s (if-let [info-fn (info-fns/mapping k)]
                                   (do
                                    (str k " - " (info-fn [k v] ctx))))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable t
                             (str "*info-error* " k))) ; TODO this try/catch FIXME design error
                      (when (map? v)
                        (str "\n" (text v ctx))))))
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

(defmethod text :info/item [item _ctx]
  (assert (valid-item? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (info-fns/stats-modifiers-info (:stats/modifiers item)) "[]"))])))

(comment
 (let [item (get (:inventory.slot/shield (:entity/inventory @(:ctx/player-eid @dev.application/state)))
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
                           (:skills/spawn (:entity/skills @(:ctx/player-eid @dev.application/state))))))
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
