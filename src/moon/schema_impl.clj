(ns moon.schema-impl
  (:require clojure.edn
            [moon.ui.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui :as ui]
            [moon.ui.group :as group]
            [moon.stage :as stage]
            [moon.ui.select-box :as select-box]
            [moon.ui.table :as table]
            [moon.ui.text-field :as text-field]
            [moon.ui.widget-group :as widget-group]
            [moon.db :as db]
            [moon.property :as property]
            [moon.edn :as edn]
            [moon.schema :as schema]
            [moon.string :as string]
            [moon.textures :as textures]
            [moon.txs :as txs]))

(defmethod schema/create :s/number
  [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/text-field
    :text (edn/->str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod schema/value :s/number
  [_  widget _schemas]
  (clojure.edn/read-string (text-field/text widget)))

(defn- add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (group/clear-children! table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (widget-group/pack! (actor/find-ancestor table ui/window?)))]
    (table/add-rows!
     table
     [[{:actor (actor/create
                {:type :ui/text-button
                 :text "+"
                 :skin skin
                 :actor/listeners {:listener/change (fn [event _actor]
                                                      (let [{:keys [ctx/db
                                                                    ctx/skin
                                                                    ctx/stage
                                                                    ctx/textures]
                                                             :as ctx} (:stage/ctx (event/stage event))]
                                                        (stage/add-actor!
                                                         stage
                                                         {:type :ui/property-overview-window
                                                          :db db
                                                          :textures textures
                                                          :skin skin
                                                          :property-type property-type
                                                          :clicked-id-fn (fn [actor id ctx]
                                                                           (actor/remove! (actor/find-ancestor actor ui/window?))
                                                                           (redo-rows ctx (conj property-ids id)))})))}})}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (actor/create
                   {:type :ui/image
                    :content (textures/texture-region textures (property/image property))
                    :actor/user-object property-id
                    :actor/listeners {:listener/text-tooltip [(property/tooltip property) skin]}})}))
      (for [id property-ids]
        {:actor (actor/create
                 {:type :ui/text-button
                  :text "-"
                  :skin skin
                  :actor/listeners {:listener/change (fn [event _actor]
                                                       (redo-rows (:stage/ctx (event/stage event))
                                                                  (disj property-ids id)))}})})])))

(defmethod schema/create :s/one-to-many [[_ property-type] property-ids ctx]
  (let [table (actor/create
               {:type :ui/table
                :table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod schema/value :s/one-to-many [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       set))

(defn- add-one-to-one-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-id]
  (let [redo-rows (fn [ctx id]
                    (group/clear-children! table)
                    (add-one-to-one-rows ctx table property-type id)
                    (widget-group/pack! (actor/find-ancestor table ui/window?)))]
    (table/add-rows!
     table
     [[(when-not property-id
         {:actor (actor/create
                  {:type :ui/text-button
                   :text "+"
                   :skin skin
                   :actor/listeners {:listener/change (fn [event _actor]
                                                        (let [{:keys [ctx/db
                                                                      ctx/skin
                                                                      ctx/stage
                                                                      ctx/textures]
                                                               :as ctx} (:stage/ctx (event/stage event))]
                                                          (stage/add-actor!
                                                           stage
                                                           {:type :ui/property-overview-window
                                                            :db db
                                                            :textures textures
                                                            :skin skin
                                                            :property-type property-type
                                                            :clicked-id-fn (fn [actor id ctx]
                                                                             (actor/remove! (actor/find-ancestor actor ui/window?))
                                                                             (redo-rows ctx id))})))}})})]
      [(when property-id
         (let [property (db/get-raw db property-id)]
           {:actor (actor/create
                    {:type :ui/image
                     :content (textures/texture-region textures (property/image property))
                     :actor/user-object property-id
                     :actor/listeners {:listener/text-tooltip [(property/tooltip property) skin]}})}))]
      [(when property-id
         {:actor (actor/create
                  {:type :ui/text-button
                   :text "-"
                   :skin skin
                   :actor/listeners {:listener/change (fn [event _actor]
                                                        (redo-rows (:stage/ctx (event/stage event))
                                                                   nil))}})})]])))

(defmethod schema/create :s/one-to-one [[_ property-type] property-id ctx]
  (let [table (actor/create
               {:type :ui/table
                :table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod schema/value :s/one-to-one [_  widget _schemas]
  (->> (group/children widget)
       (keep actor/user-object)
       first))

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (actor/remove! (actor/find-ancestor actor ui/window?))
    (widget-group/pack! (actor/find-ancestor table ui/window?))
    (let [[k _] (actor/user-object table)]
      (actor/set-user-object! table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [{:keys [ctx/audio
               ctx/skin
               ctx/stage]}]
    (stage/add-actor! stage
                      {:type :ui/window
                       :title "Choose"
                       :skin skin
                       :window/close-button? skin
                       :window/modal? true
                       :table/rows
                       [[(let [table (actor/create
                                      {:type :ui/table
                                       :table/cell-defaults {:pad 5}
                                       :table/rows (for [sound-name (map first audio)]
                                                     [{:actor (actor/create
                                                               {:type :ui/text-button
                                                                :text sound-name
                                                                :skin skin
                                                                :actor/listeners {:listener/change
                                                                                  (fn [event actor]
                                                                                    ((rebuild-sound-widget! table sound-name) actor (:stage/ctx (event/stage event))))}})}
                                                      {:actor (actor/create
                                                               {:type :ui/text-button
                                                                :text "play!"
                                                                :skin skin
                                                                :actor/listeners {:listener/change (fn [event _actor]
                                                                                                     (txs/handle! (:stage/ctx (event/stage event))
                                                                                                                  [[:tx/sound sound-name]]))}})}])} )]
                           {:actor (actor/create {:type :ui/scroll-pane
                                                  :actor table
                                                  :skin skin})
                            :width  (+ (actor/width table) 50)
                            :height (min (- (stage/viewport-height stage) 50)
                                         (actor/height table))})]]})))

(defn- sound-columns [skin table sound-name]
  [{:actor (actor/create
            {:type :ui/text-button
             :text sound-name
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  ((open-select-sounds-handler table) (:stage/ctx (event/stage event))))}})}
   {:actor (actor/create
            {:type :ui/text-button
             :text "play!"
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  (txs/handle! (:stage/ctx (event/stage event))
                                                               [[:tx/sound sound-name]]))}})}])

(defmethod schema/create :s/sound [_  sound-name {:keys [ctx/skin]}]
  (let [table (actor/create
               {:type :ui/table
                :table/cell-defaults {:pad 5}})]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor
                                (actor/create
                                 {:type :ui/text-button
                                  :text "No sound"
                                  :skin skin
                                  :actor/listeners {:listener/change
                                                    (fn [event _actor]
                                                      ((open-select-sounds-handler table) (:stage/ctx (event/stage event))))}})}])])
    table))

(defmethod schema/create :s/string [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/text-field
    :text (str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod schema/value :s/string [_ widget _schemas]
  (text-field/text widget))

(defmethod schema/create :s/val-max
  [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/text-field
    :text (edn/->str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod schema/value :s/val-max
  [_  widget _schemas]
  (clojure.edn/read-string (text-field/text widget)))
