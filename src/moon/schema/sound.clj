(ns moon.schema.sound
  (:require [gdl.scene2d.event :as event]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.ui.widget-group :as widget-group]
            [gdl.viewport :as viewport]
            [moon.actor :as actor]
            [moon.scroll-pane-cell :as scroll-pane-cell]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.txs :as txs]
            [moon.ui :as ui]))

(defn malli-form [_ _schemas]
  :string)

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
                      (ui/create
                       {:type :ui/window
                        :title "Choose"
                        :skin skin
                        :window/close-button? skin
                        :window/modal? true
                        :table/rows
                        [[(scroll-pane-cell/create
                           skin
                           (viewport/world-width (stage/viewport stage))
                           (for [sound-name (map first audio)]
                             [{:actor (ui/create
                                       {:type :ui/text-button
                                        :text sound-name
                                        :skin skin
                                        :actor/listeners {:listener/change
                                                          (fn [event actor]
                                                            ((rebuild-sound-widget! table sound-name) actor (stage/ctx (event/stage event))))}})}
                              {:actor (ui/create
                                       {:type :ui/text-button
                                        :text "play!"
                                        :skin skin
                                        :actor/listeners {:listener/change (fn [event _actor]
                                                                             (txs/handle! (stage/ctx (event/stage event))
                                                                                          [[:tx/sound sound-name]]))}})}]))]]}))))

(defn- sound-columns [skin table sound-name]
  [{:actor (ui/create
            {:type :ui/text-button
             :text sound-name
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  ((open-select-sounds-handler table) (stage/ctx (event/stage event))))}})}
   {:actor (ui/create
            {:type :ui/text-button
             :text "play!"
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  (txs/handle! (stage/ctx (event/stage event))
                                                               [[:tx/sound sound-name]]))}})}])

(defn create [_  sound-name {:keys [ctx/skin]}]
  (let [table (ui/create
               {:type :ui/table
                :table/cell-defaults {:pad 5}})]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor
                                (ui/create
                                 {:type :ui/text-button
                                  :text "No sound"
                                  :skin skin
                                  :actor/listeners {:listener/change
                                                    (fn [event _actor]
                                                      ((open-select-sounds-handler table) (stage/ctx (event/stage event))))}})}])])
    table))
