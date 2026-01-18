(ns moon.schema.sound
  (:require [moon.audio :as audio]
            [moon.ui :as ui]
            [moon.ui.table :as table]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window]
            [moon.ui.scroll-pane-cell :as scroll-pane-cell])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Stage)))

(defn malli-form [_ _schemas]
  :string)

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (.clearChildren table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (.remove (window/find-ancestor actor))
    (.pack (window/find-ancestor table))
    (let [[k _] (Actor/.getUserObject table)]
      (.setUserObject table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [_actor {:keys [ctx/audio
                      ctx/skin
                      ctx/stage]}]
    (Stage/.addActor stage
                     (ui/actor
                      {:type :ui/window
                       :skin skin
                       :title "Choose"
                       :modal? true
                       :close-button? true
                       :center? true
                       :close-on-escape? true
                       :rows [[(scroll-pane-cell/create skin
                                                        (.getWorldWidth (.getViewport stage))
                                                        (for [sound-name (audio/sound-names audio)]
                                                          [{:actor (text-button/create
                                                                    {:text sound-name
                                                                     :on-clicked (rebuild-sound-widget! table sound-name)
                                                                     :skin skin})}
                                                           {:actor (text-button/create
                                                                    {:text "play!"
                                                                     :on-clicked (fn [_actor {:keys [ctx/audio]}]
                                                                                   (audio/play! audio sound-name))
                                                                     :skin skin})}]))]]
                       :pack? true}))))

(defn- sound-columns [skin table sound-name]
  [{:actor (text-button/create
            {:text sound-name
             :on-clicked (open-select-sounds-handler table)
             :skin skin})}
   {:actor (text-button/create
            {:text "play!"
             :on-clicked (fn [_actor {:keys [ctx/audio]}]
                           (audio/play! audio sound-name))
             :skin skin})}])


(defn create [_  sound-name {:keys [ctx/skin]}]
  (let [table (ui/actor
               {:type :ui/table
                :cell-defaults {:pad 5}})]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor (text-button/create
                                        {:text "No sound"
                                         :on-clicked (open-select-sounds-handler table)
                                         :skin skin})}])])
    table))
