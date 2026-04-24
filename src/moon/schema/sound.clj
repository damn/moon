(ns moon.schema.sound
  (:require [clojure.scene2d.event :as event]
            [clojure.scene2d.group :as group]
            [clojure.scene2d.ui.widget-group :as widget-group]
            [clojure.graphics.viewport :as viewport]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.stage :as stage]
            [clojure.scene2d.ui.table :as table]
            [moon.txs :as txs]))

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (actor/remove! (actor/find-ancestor actor :ui/window))
    (widget-group/pack! (actor/find-ancestor table :ui/window))
    (let [[k _] (actor/user-object table)]
      (actor/set-user-object! table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [{:keys [ctx/audio
               ctx/skin
               ctx/stage]}]
    (stage/add-actor! stage
                      (actor/create
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
                                                                                     ((rebuild-sound-widget! table sound-name) actor (stage/ctx (event/stage event))))}})}
                                                       {:actor (actor/create
                                                                {:type :ui/text-button
                                                                 :text "play!"
                                                                 :skin skin
                                                                 :actor/listeners {:listener/change (fn [event _actor]
                                                                                                      (txs/handle! (stage/ctx (event/stage event))
                                                                                                                   [[:tx/sound sound-name]]))}})}])} )]
                            {:actor (actor/create {:type :ui/scroll-pane
                                                :actor table
                                                :skin skin})
                             :width  (+ (actor/width table) 50)
                             :height (min (- (viewport/world-height (stage/viewport stage)) 50)
                                          (actor/height table))})]]}))))

(defn- sound-columns [skin table sound-name]
  [{:actor (actor/create
            {:type :ui/text-button
             :text sound-name
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  ((open-select-sounds-handler table) (stage/ctx (event/stage event))))}})}
   {:actor (actor/create
            {:type :ui/text-button
             :text "play!"
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  (txs/handle! (stage/ctx (event/stage event))
                                                               [[:tx/sound sound-name]]))}})}])

(defn create [_  sound-name {:keys [ctx/skin]}]
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
                                                      ((open-select-sounds-handler table) (stage/ctx (event/stage event))))}})}])])
    table))
