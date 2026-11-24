(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.button
  "A button is a [[clojure.com.badlogic.gdx.scenes.scene2d.ui.table]] with a checked state and additional button style fields for pressed, unpressed, and
  checked. Each time a button is clicked, the checked state is toggled. Being a table, a button can contain any other actors.
  
  The button's padding is set to the background drawable's padding when the background changes, overwriting any padding set
  manually. Padding can still be set on the button's table cells.
  
  change event is fired when the button is clicked. Cancelling the event will restore the checked button state to what is
  was previously.
  
  The preferred size of the button is determined by the background and the button contents."
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button)))

(defn button? [x]
  (instance? Button x))
