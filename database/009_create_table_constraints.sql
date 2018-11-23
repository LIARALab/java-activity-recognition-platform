SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

ALTER TABLE `sensors`
  ADD CONSTRAINT `sensor_to_node_constraint`
      FOREIGN KEY (`node_identifier`)
      REFERENCES `nodes` (`identifier`)
      ON DELETE CASCADE
      ON UPDATE CASCADE;

ALTER TABLE `correlations_of_states`
  ADD CONSTRAINT `correlation_to_slave_state_constraint`
      FOREIGN KEY (`slave_identifier`)
      REFERENCES `states` (`identifier`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  ADD CONSTRAINT `correlation_to_master_state_constraint`
      FOREIGN KEY (`master_identifier`)
      REFERENCES `states` (`identifier`)
      ON DELETE CASCADE
      ON UPDATE CASCADE;

ALTER TABLE `states`
  ADD CONSTRAINT `state_to_sensor_constraint`
      FOREIGN KEY (`sensor_identifier`)
      REFERENCES `sensors` (`identifier`)
      ON DELETE CASCADE
      ON UPDATE CASCADE;

ALTER TABLE `states_boolean`
  ADD CONSTRAINT `state_boolean_to_state_constraint`
      FOREIGN KEY (`state_identifier`)
      REFERENCES `states` (`identifier`)
      ON DELETE CASCADE
      ON UPDATE CASCADE;

ALTER TABLE `states_double`
  ADD CONSTRAINT `state_double_to_state_constraint`
      FOREIGN KEY (`state_identifier`)
      REFERENCES `states` (`identifier`)
      ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `states_integer`
  ADD CONSTRAINT `state_integer_to_state_constraint`
      FOREIGN KEY (`state_identifier`)
      REFERENCES `states` (`identifier`)
      ON DELETE CASCADE
      ON UPDATE CASCADE;

ALTER TABLE `states_label`
  ADD CONSTRAINT `state_label_to_state_constraint`
      FOREIGN KEY (`state_identifier`)
      REFERENCES `states` (`identifier`)
      ON DELETE CASCADE
      ON UPDATE CASCADE;

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;